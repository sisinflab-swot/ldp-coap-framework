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
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.vocabulary.DCTERMS;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.UnsupportedRDFormatException;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import it.poliba.sisinflab.coap.ldp.LDP;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPException;
import it.poliba.sisinflab.coap.ldp.exception.CoAPLDPReadOnlyException;

/**
 * Manages all the read/write operations on the RDF repository
 *
 */

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
	
	/**
	 * Creates an in-memory RDF repository.
	 *
	 * @param  	baseUri		the repository base URI
	 * 
	 */
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
	
	/**
	 * Return the repository base URI
	 *
	 * @return	the base URI
	 * 
	 */
	public String getBaseURI(){
		return BASE_URI;
	}
	
	/**
	 * Adds a well-known namespace
	 *
	 * @param  	prefix		the namespace prefix
	 * @param	namespace	the namespace URI
	 * 
	 */
	public void addHandledNamespace(String prefix, String namespace){
		ns.put(prefix, namespace);
	}
	
	/**
	 * Removes a well-known namespace
	 *
	 * @param  	prefix		the prefix of the namespace to be removed
	 * 
	 */
	public void removeHandledNamespace(String prefix){
		ns.remove(prefix);
	}
	
	/**
	 * Creates a new RDF Source according to an LDP-CoAP POST request on a Basic or Direct Container
	 *
	 * @param  	uri		the URI of the resource to be created
	 * @param	rdf		the RDF text contained in the request
	 * @param	format	the RDF format of the request body
	 * 
	 */
	public void postRDFSource(String uri, String rdf, RDFFormat format) throws RDFParseException, RepositoryException, IOException, CoAPLDPException{			
		if(!this.verifyRelation(rdf, LDP.PROP_CONTAINS, format)){		
			InputStream stream = new ByteArrayInputStream(rdf.trim().getBytes(StandardCharsets.UTF_8));
			con.add(stream, uri, format);
			stream.close();		
		} else 
			throw new CoAPLDPException(LDP.PROP_CONTAINS + " not allowed in POST/PUT requests.");		
	}
	
	/**
	 * Verifies if a resource was previously deleted from the server
	 *
	 * @param  	uri		the URI of the deleted resource
	 * 
	 * @return	true if the resource was deleted
	 */
	public boolean isDeleted(String uri){
		uri = this.BASE_URI + uri;
		return deleted.contains(uri);
	}
	
	/**
	 * Creates a new RDF Source according to an LDP-CoAP POST request on an Indirect Container
	 *
	 * @param  	uri		the URI of the resource to be created
	 * @param	rdf		the RDF text contained in the request
	 * @param	rel 	the insertedContentRelation property of the Indirect Container
	 * @param	format	the RDF format of the request body
	 * 
	 */
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
		return m.contains(null, f.createIRI(rel), null);
	}
	
	private String getIndirectResourceName(String rdf, String rel, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		String name = null;
		
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		if(m.contains(null, f.createIRI(rel), null)){
			name = m.match(null, f.createIRI(rel), null).next().getObject().toString();
		}
		
		return name;
	}
	
	/**
	 * Returns the memberRelation property contained in a set of RDF statements 
	 *
	 * @param	rdf		the set of RDF statements
	 * @param	format	the RDF format of the input set
	 * 
	 * @return	the memberRelation property URI (null, if not present)
	 */
	public String getMemberRelation(String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		String name = null;
		
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		if(m.contains(null, f.createIRI(LDP.PROP_HAS_MEMBER_RELATION), null)){
			name = m.match(null, f.createIRI(LDP.PROP_HAS_MEMBER_RELATION), null).next().getObject().toString();
		}
		
		return name;
	}
	
	/**
	 * Returns the isMemberOfRelation property contained in a set of RDF statements 
	 *
	 * @param	rdf		the set of RDF statements
	 * @param	format	the RDF format of the input set
	 * 
	 * @return	the isMemberOfRelation property URI (null, if not present)
	 */
	public String getIsMemberOfRelation(String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		String name = null;
		
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		if(m.contains(null, f.createIRI(LDP.PROP_IS_MEMBER_OF_RELATION), null)){
			name = m.match(null, f.createIRI(LDP.PROP_IS_MEMBER_OF_RELATION), null).next().getObject().toString();
		}
		
		return name;
	}
	
	/**
	 * Returns the memberResource contained in a set of RDF statements 
	 *
	 * @param	rdf		the set of RDF statements
	 * @param	format	the RDF format of the input set
	 * 
	 * @return	the memberResource URI (null, if not present)
	 */
	public String getMemberResource(String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException {
		String name = null;
		
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		if(m.contains(null, f.createIRI(LDP.PROP_MEMBERSHIP_RESOURCE), null)){
			name = m.match(null, f.createIRI(LDP.PROP_MEMBERSHIP_RESOURCE), null).next().getObject().toString();
		}
		
		return name;
	}

	/**
	 * Creates/modifies a new RDF Source according to an LDP-CoAP PUT request on a Container
	 *
	 * @param  	uri		the URI of the resource to be created/modified
	 * @param	rdf		the RDF text contained in the request
	 * @param	format	the RDF format of the request body
	 * 
	 */
	public void putRDFSource(String name, String rdf, RDFFormat format) throws RDFParseException, RepositoryException, IOException, CoAPLDPException{
		if(!this.verifyReadOnly(name, rdf, format)){
			if(!this.verifyNotPersisted(rdf, format)){
				ValueFactory f = repo.getValueFactory();
				IRI node = f.createIRI(name);
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
	
	/**
	 * Returns the RDF graph associated to a resource in a Turtle serialization
	 *
	 * @param  	uri		the URI of the resource 
	 * 
	 * @return the RDF resource graph
	 */
	public String getTurtleResourceGraph(String uri){
		return this.getFullResourceTuple(uri, RDFFormat.TURTLE);
	}
	
	/**
	 * Returns the RDF graph associated to a resource in a Turtle serialization
	 *
	 * @param  	uri			the URI of the resource 
	 * @param	include		the list of include preference headers
	 * @param	omi			the list of omit preference headers
	 * 
	 * @return the RDF resource graph
	 */
	public String getTurtleResourceGraph(String uri, List<String> include, List<String> omit){		
		if (!include.isEmpty() && include.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MINIMAL_CONTAINER)){
			if (include.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MEMBERSHIP))
				return getMinimalWithMemberResourceTuple(uri, RDFFormat.TURTLE);
			else
				return getMinimalResourceTuple(uri, RDFFormat.TURTLE); 
		} else if (!omit.isEmpty()){
			if(omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MEMBERSHIP) && omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_CONTAINMENT))
				return getMinimalResourceTuple(uri, RDFFormat.TURTLE);
			else if (!omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MEMBERSHIP) && omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_CONTAINMENT))
				return getMinimalWithMemberResourceTuple(uri, RDFFormat.TURTLE);
			else if (omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_MEMBERSHIP) && !omit.contains(LDP.LINK_LDP+":"+LDP.PREFER_LNAME_CONTAINMENT))
				return getNoContainResourceTuple(uri, RDFFormat.TURTLE);
		} 
		
		return this.getFullResourceTuple(uri, RDFFormat.TURTLE); 
	}
	
	/**
	 * Returns the RDF graph associated to a resource in a JSON-LD serialization
	 *
	 * @param  	uri		the URI of the resource 
	 * 
	 * @return the RDF resource graph
	 */
	public String getJSONLDResourceGraph(String uri){
		return this.getFullResourceTuple(uri, RDFFormat.JSONLD);
	}
	
	private String getNoContainResourceTuple(String res, RDFFormat format) {
    	try {    		
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		RDFWriter writer = Rio.createWriter(format, out);  
    		
    		writer.startRDF();
    		
    		for(String prefix : ns.keySet()){
    			writer.handleNamespace(prefix, ns.get(prefix));
    		}
    		
    		IRI uRes = createIRI(res);
    		String hasMemberRel = getHasMemberRelationFromContainer(uRes);
    		String isMemberOfRel = getIsMemberOfRelationFromContainer(uRes);
    		
    		RepositoryResult<Statement> results = con.getStatements(uRes, null, null, false);
    		while(results.hasNext()){
    			Statement s = results.next();
    			if(!s.getPredicate().stringValue().equals(LDP.PROP_MEMBER) 
    					&& !s.getPredicate().stringValue().equals(hasMemberRel)
    					&& !s.getPredicate().stringValue().equals(isMemberOfRel) )
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
	
	private String getHasMemberRelationFromContainer(IRI res){
		String hasMemberRel = "";
		try {
			RepositoryResult<Statement> results = con.getStatements(res, createIRI(LDP.PROP_HAS_MEMBER_RELATION), null, false);
			while(results.hasNext()){
				Statement s = results.next();
				hasMemberRel = s.getObject().stringValue();
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hasMemberRel;
	}
	
	private String getIsMemberOfRelationFromContainer(IRI res){
		String isMemberOfRel = "";
		try {
			RepositoryResult<Statement> results = con.getStatements(res, createIRI(LDP.PROP_IS_MEMBER_OF_RELATION), null, false);
			while(results.hasNext()){
				Statement s = results.next();
				isMemberOfRel = s.getObject().stringValue();
			}
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isMemberOfRel;
	}
	
	private String getMinimalWithMemberResourceTuple(String res, RDFFormat format) {
    	try {    		
    		String member = null;
    		ByteArrayOutputStream out = new ByteArrayOutputStream();
    		RDFWriter writer = Rio.createWriter(format, out);  
    		
    		writer.startRDF();
    		
    		for(String prefix : ns.keySet()){
    			writer.handleNamespace(prefix, ns.get(prefix));
    		}
    		
    		RepositoryResult<Statement> results = con.getStatements(createIRI(res), null, null, false);
    		while(results.hasNext()){
    			Statement s = results.next();
    			
    			if(!s.getPredicate().stringValue().equals(LDP.PROP_CONTAINS))
    				writer.handleStatement(s);
    			
    			if(s.getPredicate().stringValue().equals(LDP.PROP_MEMBERSHIP_RESOURCE))
    				member = s.getObject().stringValue();
    		}
    		
    		if(member != null)
    			this.writeStatement(writer, member);
    		  		
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
    		
    		IRI uRes = createIRI(res);
    		String hasMemberRel = getHasMemberRelationFromContainer(uRes);
    		String isMemberOfRel = getIsMemberOfRelationFromContainer(uRes);
    		
    		RepositoryResult<Statement> results = con.getStatements(uRes, null, null, false);
    		while(results.hasNext()){
    			Statement s = results.next();
    			if(!s.getPredicate().stringValue().equals(LDP.PROP_CONTAINS) 
    					&& !s.getPredicate().stringValue().equals(LDP.PROP_MEMBER) 
    					&& !s.getPredicate().stringValue().equals(hasMemberRel)
    					&& !s.getPredicate().stringValue().equals(isMemberOfRel) )
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
		RepositoryResult<Statement> results = con.getStatements(this.createIRI(res), null, null, false);
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
	
	/**
	 * Adds a single statement to the RDF repository 
	 *
	 * @param  	s	the statement subject
	 * @param	p	the statement predicate 
	 * @param	o	the statement object
	 * 
	 */
	public void addRDFStatement(Resource s, IRI p, Value o){
		try {
			con.add(s, p, o);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds the ldp:RDFSource type to the resource	 
	 *
	 * @param  	uri		the URI of the resource 
	 * 
	 */
	public void addRDFSource(String uri){		
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);		
		addRDFStatement(node, RDF.TYPE, f.createIRI(LDP.CLASS_RDFSOURCE));
	}
	
	/**
	 * Adds a specific type to the resource
	 *
	 * @param  	uri		the URI of the resource 
	 * @param	type	the type of resource
	 * 
	 */
	public void addRDFSource(String name, String type){		
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(name);		
		addRDFStatement(node, RDF.TYPE, f.createIRI(type));
	}
	
	/**
	 * Closes the RDF repository
	 * 
	 */
	public void close(){
		try {
			con.close();
			repo.shutDown();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Adds the ldp:Container type to the resource	 
	 *
	 * @param  	uri		the URI of the resource 
	 * 
	 */
	public void addRDFContainer(String uri) {
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);		
		addRDFStatement(node, RDF.TYPE, f.createIRI(LDP.CLASS_CONTAINER));		
	}

	/**
	 * Adds the ldp:BasicContainer type to the resource	 
	 *
	 * @param  	uri		the URI of the resource 
	 * 
	 */
	public void addRDFBasicContainer(String uri) {
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);		
		addRDFStatement(node, RDF.TYPE, f.createIRI(LDP.CLASS_BASIC_CONTAINER));
	}
	
	/**
	 * Adds the ldp:DirectContainer type to the resource	 
	 *
	 * @param  	uri		the URI of the resource 
	 * 
	 */
	public void addRDFDirectContainer(String uri) {
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);		
		addRDFStatement(node, RDF.TYPE, f.createIRI(LDP.CLASS_DIRECT_CONTAINER));
	}
	
	/**
	 * Adds the ldp:IndirectContainer type to the resource	 
	 *
	 * @param  	uri		the URI of the resource 
	 * 
	 */
	public void addRDFIndirectContainer(String uri) {
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);		
		addRDFStatement(node, RDF.TYPE, f.createIRI(LDP.CLASS_INDIRECT_CONTAINER));
	}

	/**
	 * Adds the RDF description statement to the resource	 
	 *
	 * @param  	uri				the URI of the resource
	 * @param	description 	the resource description
	 * 
	 */
	public void setRDFDescription(String uri, String description) {
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);		
		addRDFStatement(node, DCTERMS.DESCRIPTION, f.createLiteral(description));		
	}
	
	/**
	 * Adds the RDF title statement to the resource	 
	 *
	 * @param  	uri		the URI of the resource
	 * @param	title 	the resource title
	 * 
	 */
	public void setRDFTitle(String uri, String title){
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);		
		addRDFStatement(node, DCTERMS.TITLE, f.createLiteral(title));		
	}	
	
	/**
	 * Updates a single statement of the RDF repository 
	 *
	 * @param  	subj	the statement subject
	 * @param	pred	the statement predicate 
	 * @param	obj		the statement object
	 * 
	 */
	public void updateRDFStatement(String subj, String pred, String obj) {
		ValueFactory f = repo.getValueFactory();
		IRI s = f.createIRI(subj);
		IRI p = f.createIRI(pred);
		IRI o = f.createIRI(obj);
		
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
	
	/**
	 * Updates a single statement of the RDF repository (object is a literal) 
	 *
	 * @param  	subj	the statement subject
	 * @param	pred	the statement predicate 
	 * @param	obj		the statement object (a Double value)
	 * 
	 */
	public void updateRDFLiteralStatement(String subj, String pred, Double obj) {
		ValueFactory f = repo.getValueFactory();
		IRI s = f.createIRI(subj);
		IRI p = f.createIRI(pred);
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
	
	/**
	 * Updates a single statement of the RDF repository (object is a literal) 
	 *
	 * @param  	subj	the statement subject
	 * @param	pred	the statement predicate 
	 * @param	obj		the statement object (a Date value)
	 * 
	 */
	public void updateRDFLiteralStatement(String subj, String pred, Date obj) {
		ValueFactory f = repo.getValueFactory();
		IRI s = f.createIRI(subj);
		IRI p = f.createIRI(pred);
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
	
	/**
	 * Adds the RDF created statement to the resource	 
	 *
	 * @param  	uri		the URI of the resource
	 * 
	 */
	public void setRDFCreated(String uri){
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);		
		addRDFStatement(node, DCTERMS.CREATED, f.createLiteral(new Date()));		
	}

	/**
	 * Adds the LDP contains statement	 
	 *
	 * @param  	resource	the URI of the resource
	 * @param	container 	the URI of the container
	 * 
	 */
	public void setLDPContainsRelationship(String resource, String container) {
		ValueFactory f = repo.getValueFactory();		
		addRDFStatement(f.createIRI(container), f.createIRI(LDP.PROP_CONTAINS), f.createIRI(resource));
	}

	/**
	 * Deletes all statements related to a specific LDP RDF Source
	 * 
	 * @param  	uri		the URI of the resource
	 * 
	 */
	public void deleteRDFSource(String uri){
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(uri);
		
		try {
			con.remove(con.getStatements(null, null, node, true));
			con.remove(con.getStatements(node, null, null, true));
			
			System.out.println("RDF Resource Deleted: " + uri);
			deleted.add(uri);
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Deletes all statements related to a specific LDP Basic Container
	 * 
	 * @param  	uri		the URI of the container
	 * 
	 */
	public void deleteRDFBasicContainer(String name){
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(name);
		try {
			RepositoryResult<Statement> statements = con.getStatements(node, f.createIRI(LDP.PROP_CONTAINS), null, true);
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
	
	/**
	 * Deletes all statements related to a specific LDP Direct Container
	 * 
	 * @param  	uri		the URI of the container
	 * 
	 */
	public void deleteRDFDirectContainer(String name){
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(name);
		try {
			RepositoryResult<Statement> statements = con.getStatements(node, f.createIRI(LDP.PROP_MEMBERSHIP_RESOURCE), null, true);
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
	
	/**
	 * Adds the LDP membershipResource statement	 
	 *
	 * @param  	resource	the URI of the resource
	 * @param	container 	the URI of the container
	 * 
	 */
	public void setLDPMembershipResource(String resource, String container){
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createIRI(container), f.createIRI(LDP.PROP_MEMBERSHIP_RESOURCE), f.createIRI(resource));		
	}
	
	/**
	 * Adds the LDP memberRelation statement	 
	 *
	 * @param  	resource	the URI of the resource
	 * @param	container 	the URI of the container
	 * 
	 */
	public void setLDPMemberRelation(String container, String relation){
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createIRI(container), f.createIRI(LDP.PROP_HAS_MEMBER_RELATION), f.createIRI(relation));		
	}
	
	/**
	 * Adds the LDP inverseMemberRelation statement	 
	 *
	 * @param  	resource	the URI of the resource
	 * @param	container 	the URI of the container
	 * 
	 */
	public void setLDPInverseMemberRelation(String container, String relation){
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createIRI(container), f.createIRI(LDP.PROP_IS_MEMBER_OF_RELATION), f.createIRI(relation));		
	}
	
	/**
	 * Adds the LDP insertedContentRelation statement	 
	 *
	 * @param  	resource	the URI of the resource
	 * @param	container 	the URI of the container
	 * 
	 */
	public void setLDPInsertedContentRelation(String container, String relation){
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createIRI(container), f.createIRI(LDP.PROP_INSERTED_CONTENT_RELATION), f.createIRI(relation));		
	}

	/**
	 * Updates a single statement of the RDF repository 
	 *
	 * @param  	subj	the statement subject
	 * @param	pred	the statement predicate 
	 * @param	obj		the statement object
	 * 
	 */
	public void setRDFStatement(String s, String p, String o) {
		ValueFactory f = repo.getValueFactory();
		addRDFStatement(f.createIRI(s), f.createIRI(p), f.createIRI(o));	
	}
	
	/**
	 * Updates a single statement of the RDF repository 
	 *
	 * @param  	subj	the statement subject
	 * @param	pred	the statement predicate 
	 * @param	obj		the statement object (a Double value)
	 * 
	 */
	public void setRDFStatement(String s, String p, double v) {
		ValueFactory f = repo.getValueFactory();		
		DecimalFormat ft = new DecimalFormat("#0.00");		
		addRDFStatement(f.createIRI(s), f.createIRI(p), f.createLiteral(ft.format(v), XMLSchema.DOUBLE));	
	}
	
	/**
	 * Creates an IRI from a string 
	 *
	 * @param  	uri		the string URI to be converted
	 * 
	 * @return the created IRI
	 */
	public IRI createIRI(String uri){
		return repo.getValueFactory().createIRI(uri);
	}

	/**
	 * Deletes all LDP memberResource statements related to a specific resource
	 *
	 * @param  	name	the name of the resource
	 * 
	 */
	public void deleteMemberResourceStatement(String name) {
		
		if (name.startsWith("/"))
			name = name.substring(1);
		
		ValueFactory f = repo.getValueFactory();
		IRI node = f.createIRI(this.getBaseURI() + "/" + name);		
		try {
			RepositoryResult<Statement> s = con.getStatements(node, f.createIRI(LDP.PROP_MEMBERSHIP_RESOURCE), null, true);
			while(s.hasNext()){
				Statement stat = s.next();
				con.remove(stat);
				deleted.add(stat.getObject().stringValue());
			}
		} catch (RepositoryException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a read-only property constraint
	 * 
	 * @param  	p	the URI of the read-only property
	 * 
	 */
	public void addReadOnlyProperty(String p){
		readOnly.add(p);
	}
	
	/**
	 * Adds a not-persisted property constraint
	 * 
	 * @param  	p	the URI of the not-persisted property
	 * 
	 */
	public void addNotPersistedProperty(String p){
		notPersisted.add(p);
	}
	
	/**
	 * Sets the LDP constrainedBy URI
	 * 
	 * @param  	uri		the URI of the LDP constrainedBy property
	 * 
	 */
	public void setConstrainedByURI(String uri){
		constrainedByURI = uri;
	}
	
	/**
	 * Gets the LDP constrainedBy URI
	 * 
	 * @return	the URI of the LDP constrainedBy property
	 * 
	 */
	public String getconstrainedByURI(){
		return this.constrainedByURI;
	}
	
	/**
	 * Verifies if the input RDF graph contains read-only properties for a specific resource 
	 * 
	 * @param 	uri		the resource URI
	 * @param	rdf 	the input RDF graph
	 * @param	format	the RDF format of the graph
	 * 
	 * @return	true, if the graph contains read-only properties
	 * 
	 */
	private boolean verifyReadOnly(String uri, String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException, RepositoryException{
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.trim().getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		
		IRI res = f.createIRI(uri);
		
		for(String r : readOnly){
			IRI prop = f.createIRI(r);

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
	
	/**
	 * Verifies if the input RDF graph contains not-persisted properties 
	 * 
	 * @param	rdf 	the input RDF graph
	 * @param	format	the RDF format of the graph
	 * 
	 * @return	true, if the graph contains not-persisted properties
	 * 
	 */
	private boolean verifyNotPersisted(String rdf, RDFFormat format) throws RDFParseException, UnsupportedRDFormatException, IOException{
		ValueFactory f = repo.getValueFactory();		
		InputStream stream = new ByteArrayInputStream(rdf.trim().getBytes(StandardCharsets.UTF_8));
		Model m = Rio.parse(stream, BASE_URI, format);
		
		for(String p : notPersisted){
			if (m.contains(null, f.createIRI(p), null))
				return true;
		}
		
		return false;
	}
	
	private String getMemberResource(IRI res) throws RepositoryException{
		ValueFactory f = repo.getValueFactory();
		String member = null;
		if(con.hasStatement(res, f.createIRI(LDP.PROP_MEMBERSHIP_RESOURCE), null, false)){
			member = con.getStatements(res, f.createIRI(LDP.PROP_MEMBERSHIP_RESOURCE), null, false).next().getObject().stringValue();
		} else if (con.hasStatement(null, f.createIRI(LDP.PROP_MEMBER), res, false)){
			member = con.getStatements(null, f.createIRI(LDP.PROP_MEMBER), res, false).next().getObject().stringValue();
		}
		
		return member;			
	}
	
	/**
	 * Applies PATCH request 
	 * 
	 * @param	patch	the RDF body of the patch request
	 * 
	 */
	public void patchResource(String patch) throws RepositoryException, ParseException, InvalidPatchDocumentException{
		// Apply a patch to the repository
		RdfPatchUtil.applyPatch(con, patch);
	}
	
}
