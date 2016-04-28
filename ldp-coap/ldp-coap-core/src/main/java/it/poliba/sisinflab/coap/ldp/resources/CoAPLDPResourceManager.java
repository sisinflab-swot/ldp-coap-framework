package it.poliba.sisinflab.coap.ldp.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.marmotta.platform.ldp.patch.InvalidPatchDocumentException;
import org.apache.marmotta.platform.ldp.patch.RdfPatchUtil;
import org.apache.marmotta.platform.ldp.patch.parser.ParseException;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.XMLSchema;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.UnsupportedRDFormatException;
import org.openrdf.sail.memory.MemoryStore;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPReadOnlyException;

public class CoAPLDPResourceManager {
	
	// init RDF Repository
	Repository repo;
	RepositoryConnection con;
	
	// handle namespace
	HashMap<String, String> ns;
	
	// handle read-only properties	
	ArrayList<String> readOnly;
	String constrainedByURI; 
	
	// handle not persisted properties
	ArrayList<String> notPersisted;
	
	// handle deleted resources
	ArrayList<String> deleted;
	
	String BASE_URI;
	
	public CoAPLDPResourceManager(String baseUri){
		BASE_URI = baseUri;
		repo = new SailRepository(new MemoryStore());
		try {
			repo.initialize();
			con = repo.getConnection();				
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		ns = new HashMap<String, String>();
		ns.put(DCTERMS.PREFIX, DCTERMS.NAMESPACE);
		ns.put(LDP.LINK_LDP, LDP.getNSURI());		
		
		readOnly = new ArrayList<String>();
		readOnly.add(LDP.PROP_CONTAINS);
		
		notPersisted = new ArrayList<String>();
		
		deleted = new ArrayList<String>();
	}
	
	public String getBaseURI(){
		return BASE_URI;
	}
	
	public void addHandledNamespace(String prefix, String namespace){
		ns.put(prefix, namespace);
	}
	
	public void removeHandledNamespace(String prefix){
		ns.remove(prefix);
	}
	
	public void postRDFSource(String uri, String rdf, RDFFormat format) throws RDFParseException, RepositoryException, IOException, CoAPLDPException{			
		if(!this.verifyRelation(rdf, LDP.PROP_CONTAINS, format)){		
			InputStream stream = new ByteArrayInputStream(rdf.trim().getBytes(StandardCharsets.UTF_8));
			con.add(stream, uri, format);
			stream.close();		
		} else 
			throw new CoAPLDPException(LDP.PROP_CONTAINS + " not allowed in POST/PUT requests.");		
	}
	
	public boolean isDeleted(String uri){
		uri = this.BASE_URI + uri;
		return deleted.contains(uri);
	}
	
	public String postIndirectRDFSource(String uri, String rdf, String rel, RDFFormat format) throws RDFParseException, RepositoryException, IOException, CoAPLDPException{			
		String name = null;		
		rdf = rdf.trim();
		
		if(!this.verifyRelation(rdf, LDP.PROP_CONTAINS, format)){	
			name = getIndirectResourceName(rdf, rel, format);
			//if (name != null){
				InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
				con.add(stream, uri, format);
				stream.close();		
			//} else throw new CoAPLDPException(rel + " not found in POST requests.");	
		} else 
			throw new CoAPLDPException(LDP.PROP_CONTAINS + " not allowed in POST/PUT requests.");		
		
		return name;
	}
	
	private boolean verifyRelation(String rdf, String rel, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.trim().getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		return m.contains(null, f.createURI(rel), null);
	}
	
	private String getIndirectResourceName(String rdf, String rel, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		String name = null;
		
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		if(m.contains(null, f.createURI(rel), null)){
			name = m.match(null, f.createURI(rel), null).next().getObject().toString();
		}
		
		return name;
	}
	
	public String getMemberRelation(String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		String name = null;
		
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		if(m.contains(null, f.createURI(LDP.PROP_HAS_MEMBER_RELATION), null)){
			name = m.match(null, f.createURI(LDP.PROP_HAS_MEMBER_RELATION), null).next().getObject().toString();
		}
		
		return name;
	}
	
	public String getIsMemberOfRelation(String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		String name = null;
		
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		if(m.contains(null, f.createURI(LDP.PROP_IS_MEMBER_OF_RELATION), null)){
			name = m.match(null, f.createURI(LDP.PROP_IS_MEMBER_OF_RELATION), null).next().getObject().toString();
		}
		
		return name;
	}
	
	public String getMemberResource(String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		String name = null;
		
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		if(m.contains(null, f.createURI(LDP.PROP_MEMBERSHIP_RESOURCE), null)){
			name = m.match(null, f.createURI(LDP.PROP_MEMBERSHIP_RESOURCE), null).next().getObject().toString();
		}
		
		return name;
	}

	public void putRDFSource(String name, String rdf, RDFFormat format) throws RDFParseException, RepositoryException, IOException, CoAPLDPException{
		if(!this.verifyReadOnly(name, rdf, format)){
			if(!this.verifyNotPersisted(rdf, format)){
				ValueFactory f = repo.getValueFactory();
				URI node = f.createURI(name);
				con.remove(con.getStatements(node, null, null, true));
				
				/*** POST ***/
				InputStream stream = new ByteArrayInputStream(rdf.trim().getBytes(StandardCharsets.UTF_8));
				con.add(stream, name, format);
				stream.close();		
			} else 
				throw new CoAPLDPException("Not Persisted property exception in PUT request.");
		} else 
			throw new CoAPLDPReadOnlyException("Read-only property exception in PUT request.");
	}
	
	public String getTurtleResourceGraph(String res){
		return this.getFullResourceTuple(res, RDFFormat.TURTLE);
	}
	
	public String getTurtleResourceGraph(String res, List<String> include, List<String> omit){		
		if (!include.isEmpty() && include.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MINIMAL_CONTAINER)){
			if (include.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MEMBERSHIP))
				return getMinimalWithMemberResourceTuple(res, RDFFormat.TURTLE);
			else
				return getMinimalResourceTuple(res, RDFFormat.TURTLE); 
		} else if (!omit.isEmpty()){
			if(omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MEMBERSHIP) && omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_CONTAINMENT))
				return getMinimalResourceTuple(res, RDFFormat.TURTLE);
			else if (!omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MEMBERSHIP) && omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_CONTAINMENT))
				return getMinimalWithMemberResourceTuple(res, RDFFormat.TURTLE);
			else if (omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MEMBERSHIP) && !omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_CONTAINMENT))
				return getNoContainResourceTuple(res, RDFFormat.TURTLE);
		} 
		
		return this.getFullResourceTuple(res, RDFFormat.TURTLE); 
	}
	
	public String getJSONLDResourceGraph(String res){
		return this.getFullResourceTuple(res, RDFFormat.JSONLD);
	}
	
	private String getNoContainResourceTuple(String res, RDFFormat format) {
    	try {    		
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		RDFWriter writer = Rio.createWriter(format, out);  
    		
    		writer.startRDF();
    		
    		for(String prefix : ns.keySet()){
    			writer.handleNamespace(prefix, ns.get(prefix));
    		}
    		
    		RepositoryResult<Statement> results = con.getStatements(this.createURI(res), null, null, false);
    		while(results.hasNext()){
    			Statement s = results.next();
    			if(!s.getPredicate().stringValue().equals(LDP.PROP_MEMBER) 
    					&& !s.getPredicate().stringValue().equals(LDP.PROP_IS_MEMBER_OF_RELATION))
    				writer.handleStatement(s);
    		}
    		  		
    		writer.endRDF();
    		
    		return out.toString();

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}	
    	
    	return null;
	}
	
	private String getMinimalWithMemberResourceTuple(String res, RDFFormat format) {
    	try {    		
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		RDFWriter writer = Rio.createWriter(format, out);  
    		
    		writer.startRDF();
    		
    		for(String prefix : ns.keySet()){
    			writer.handleNamespace(prefix, ns.get(prefix));
    		}
    		
    		RepositoryResult<Statement> results = con.getStatements(this.createURI(res), null, null, false);
    		while(results.hasNext()){
    			Statement s = results.next();
    			if(!s.getPredicate().stringValue().equals(LDP.PROP_CONTAINS))
    				writer.handleStatement(s);
    		}
    		  		
    		writer.endRDF();
    		
    		return out.toString();

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}	
    	
    	return null;
	}
	
	private String getMinimalResourceTuple(String res, RDFFormat format) {
    	try {    		
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		RDFWriter writer = Rio.createWriter(format, out);  
    		
    		writer.startRDF();
    		
    		for(String prefix : ns.keySet()){
    			writer.handleNamespace(prefix, ns.get(prefix));
    		}
    		
    		RepositoryResult<Statement> results = con.getStatements(this.createURI(res), null, null, false);
    		while(results.hasNext()){
    			Statement s = results.next();
    			if(!s.getPredicate().stringValue().equals(LDP.PROP_CONTAINS) && !s.getPredicate().stringValue().equals(LDP.PROP_MEMBER) 
    					&& !s.getPredicate().stringValue().equals(LDP.PROP_IS_MEMBER_OF_RELATION))
    				writer.handleStatement(s);
    		}
    		  		
    		writer.endRDF();
    		
    		return out.toString();

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}	
    	
    	return null;
	}
	
	private String getFullResourceTuple(String res, RDFFormat format) {
    	try {    		
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		RDFWriter writer = Rio.createWriter(format, out);  
    		
    		writer.startRDF();
    		
    		for(String prefix : ns.keySet()){
    			writer.handleNamespace(prefix, ns.get(prefix));
    		}    		
    		
    		writeStatement(writer, res);    		
    		writer.endRDF();
    		
    		return out.toString();

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (RDFHandlerException e) {
			e.printStackTrace();
		}	
    	
    	return null;
	}
	
	private void writeStatement(RDFWriter writer, String res) throws RDFHandlerException, RepositoryException {
		String member = null;
		RepositoryResult<Statement> results = con.getStatements(this.createURI(res), null, null, false);
		while(results.hasNext()){
			Statement s = results.next();
			writer.handleStatement(s);
			
			if(s.getPredicate().stringValue().equals(LDP.PROP_MEMBERSHIP_RESOURCE))
				member = s.getObject().stringValue();
		}
		
		if(member != null)
			this.writeStatement(writer, member);		
	}

	private String noPrefix(String s){
		String out = "";
		String[] lines = s.split(System.getProperty("line.separator"));
		for(int i=0; i<lines.length; i++) {		  
		  if(!lines[i].startsWith("@prefix"))
			  out = out.concat(lines[i] + "\n");
		}
		return out.trim();
	}
	
	public void addRDFStatement(Resource s, URI p, Value o){
		try {
			con.add(s, p, o);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addRDFSource(String name){		
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, RDF.TYPE, f.createURI(LDP.CLASS_RDFSOURCE));
	}
	
	public void addRDFSource(String name, String type){		
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, RDF.TYPE, f.createURI(type));
	}
	
	public void close(){
		try {
			con.close();
			repo.shutDown();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addRDFContainer(String name) {
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, RDF.TYPE, f.createURI(LDP.CLASS_CONTAINER));		
	}

	public void addRDFBasicContainer(String name) {
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, RDF.TYPE, f.createURI(LDP.CLASS_BASIC_CONTAINER));
	}
	
	public void addRDFDirectContainer(String name) {
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, RDF.TYPE, f.createURI(LDP.CLASS_DIRECT_CONTAINER));
	}
	
	public void addRDFIndirectContainer(String name) {
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, RDF.TYPE, f.createURI(LDP.CLASS_INDIRECT_CONTAINER));
	}

	public void setRDFDescription(String name, String description) {
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, DCTERMS.DESCRIPTION, f.createLiteral(description));		
	}
	
	public void setRDFTitle(String name, String title){
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, DCTERMS.TITLE, f.createLiteral(title));		
	}	
	
	public void updateRDFStatement(String subj, String pred, String obj) {
		ValueFactory f = repo.getValueFactory();
		URI s = f.createURI(subj);
		URI p = f.createURI(pred);
		URI o = f.createURI(obj);
		
		try {
			RepositoryConnection c_upd = repo.getConnection();
			c_upd.remove(c_upd.getStatements(s, p, null, false));
			c_upd.add(s, p, o);
			c_upd.close();
		} catch (RepositoryException | IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void updateRDFLiteralStatement(String subj, String pred, Double obj) {
		ValueFactory f = repo.getValueFactory();
		URI s = f.createURI(subj);
		URI p = f.createURI(pred);
		DecimalFormat ft = new DecimalFormat("#0.00");		
		Literal o = f.createLiteral(ft.format(obj), XMLSchema.DOUBLE);
		
		try {
			RepositoryConnection c_upd = repo.getConnection();
			c_upd.remove(c_upd.getStatements(s, p, null, false));
			c_upd.add(s, p, o);
			c_upd.close();
		} catch (RepositoryException | IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
	
	public void updateRDFLiteralStatement(String subj, String pred, Date obj) {
		ValueFactory f = repo.getValueFactory();
		URI s = f.createURI(subj);
		URI p = f.createURI(pred);
		Literal o = f.createLiteral(obj);
		
		try {
			RepositoryConnection c_upd = repo.getConnection();
			c_upd.remove(c_upd.getStatements(s, p, null, false));
			c_upd.add(s, p, o);
			c_upd.close();
		} catch (RepositoryException | IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void setRDFCreated(String name){
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);		
		addRDFStatement(node, DCTERMS.CREATED, f.createLiteral(new Date()));		
	}

	public void setLDPContainsRelationship(String resource, String container) {
		ValueFactory f = repo.getValueFactory();		
		addRDFStatement(f.createURI(container), f.createURI(LDP.PROP_CONTAINS), f.createURI(resource));
	}

	public void deleteRDFSource(String name){
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);
		
		try {
			con.remove(con.getStatements(null, null, node, true));
			con.remove(con.getStatements(node, null, null, true));
			
			System.out.println("RDF Resource Deleted: " + name);
			deleted.add(name);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteRDFBasicContainer(String name){
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);
		try {
			RepositoryResult<Statement> statements = con.getStatements(node, f.createURI(LDP.PROP_CONTAINS), null, true);
			while(statements.hasNext()){ 
				Statement s = statements.next();
				String obj = s.getObject().stringValue();
				this.deleteRDFBasicContainer(obj);
			}
			this.deleteRDFSource(name);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteRDFDirectContainer(String name){
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(name);
		try {
			RepositoryResult<Statement> statements = con.getStatements(node, f.createURI(LDP.PROP_MEMBERSHIP_RESOURCE), null, true);
			while(statements.hasNext()){ 
				Statement s = statements.next();
				String obj = s.getObject().stringValue();
				this.deleteRDFSource(obj);								
			}
			this.deleteRDFBasicContainer(name);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setLDPMembershipResource(String resource, String container){
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createURI(container), f.createURI(LDP.PROP_MEMBERSHIP_RESOURCE), f.createURI(resource));		
	}
	
	public void setLDPMemberRelation(String container, String relation){
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createURI(container), f.createURI(LDP.PROP_HAS_MEMBER_RELATION), f.createURI(relation));		
	}
	
	public void setLDPInverseMemberRelation(String container, String relation){
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createURI(container), f.createURI(LDP.PROP_IS_MEMBER_OF_RELATION), f.createURI(relation));		
	}
	
	public void setLDPInsertedContentRelation(String container, String relation){
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createURI(container), f.createURI(LDP.PROP_INSERTED_CONTENT_RELATION), f.createURI(relation));		
	}

	public void setRDFStatement(String s, String p, String o) {
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createURI(s), f.createURI(p), f.createURI(o));	
	}
	
	public void setRDFStatement(String s, String p, double v) {
		ValueFactory f = repo.getValueFactory();		
		DecimalFormat ft = new DecimalFormat("#0.00");		
		addRDFStatement(f.createURI(s), f.createURI(p), f.createLiteral(ft.format(v), XMLSchema.DOUBLE));	
	}
	
	public URI createURI(String uri){
		return repo.getValueFactory().createURI(uri);
	}

	public void deleteMemberResourceStatement(String title) {
		
		if (title.startsWith("/"))
			title = title.substring(1);
		
		ValueFactory f = repo.getValueFactory();
		URI node = f.createURI(this.getBaseURI() + "/" + title);		
		try {
			RepositoryResult<Statement> s = con.getStatements(node, f.createURI(LDP.PROP_MEMBERSHIP_RESOURCE), null, true);
			while(s.hasNext()){
				Statement stat = s.next();
				con.remove(stat);
				deleted.add(stat.getObject().stringValue());
			}
		} catch (RepositoryException e) {			
			e.printStackTrace();
		}
	}
	
	public void setReadOnlyProperty(String p){
		readOnly.add(p);
	}
	
	public void setNotPersistedProperty(String p){
		notPersisted.add(p);
	}
	
	public void setConstrainedByURI(String uri){
		constrainedByURI = uri;
	}
	
	public String getconstrainedByURI(){
		return this.constrainedByURI;
	}
	
	private boolean verifyReadOnly(String name, String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException, RepositoryException{
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.trim().getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		
		URI res = f.createURI(name);
		
		for(String r : readOnly){
			URI prop = f.createURI(r);

			ArrayList<String> oldList = new ArrayList<String>();
			RepositoryResult<Statement> oldStat = con.getStatements(res, prop, null, false);
			while(oldStat.hasNext()){
				oldList.add(oldStat.next().getObject().stringValue());
			}
			
			ArrayList<String> newList = new ArrayList<String>();
			Iterator<Statement> newStat = m.match(null, prop, null);
			while(newStat.hasNext()){
				newList.add(newStat.next().getObject().stringValue());
			}				
			
			if (!oldList.equals(newList))
				return true;
		}			
		
		return false;
	}
	
	private boolean verifyNotPersisted(String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException{
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.trim().getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		
		for(String p : notPersisted){
			if (m.contains(null, f.createURI(p), null))
				return true;
		}
		
		return false;
	}
	
	private String getMemberResource(URI res) throws RepositoryException{
		ValueFactory f = repo.getValueFactory();
		String member = null;
		if(con.hasStatement(res, f.createURI(LDP.PROP_MEMBERSHIP_RESOURCE), null, false)){
			member = con.getStatements(res, f.createURI(LDP.PROP_MEMBERSHIP_RESOURCE), null, false).next().getObject().stringValue();
		} else if (con.hasStatement(null, f.createURI(LDP.PROP_MEMBER), res, false)){
			member = con.getStatements(null, f.createURI(LDP.PROP_MEMBER), res, false).next().getObject().stringValue();
		}
		
		return member;			
	}
	
	public void patchResource(String patch) throws RepositoryException, ParseException, InvalidPatchDocumentException{
		// Apply a patch to the repository
		RdfPatchUtil.applyPatch(con, patch);
	}
	
}
