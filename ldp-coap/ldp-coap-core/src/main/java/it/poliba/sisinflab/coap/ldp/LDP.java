package it.poliba.sisinflab.coap.ldp;

public class LDP {
	
	protected static final String NS = "http://www.w3.org/ns/ldp#";

	/**
	 * The namespace of the vocabulary as a string
	 */
	public static String getNSURI()
	{
		return NS;
	}

	// Content types
	/*public static final String CT_APPLICATION_JSON = MediaType.APPLICATION_JSON;
	public static final String CT_APPLICATION_LD_JSON = "application/ld+json";
	public static final String CT_APPLICATION_RDFXML = "application/rdf+xml";
	public static final String CT_APPLICATION_XTURTLE = "application/x-turtle";
	public static final String CT_APPLICATION_SPARQLQUERY = "application/sparql-query";
	public static final String CT_APPLICATION_SPARQLRESULTSXML = "application/sparql-results+xml";
	public static final String CT_APPLICATION_SPARQLRESULTSJSON = "application/sparql-results+json";
	public static final String CT_TEXT_HTML = MediaType.TEXT_HTML;
	public static final String CT_TEXT_TURTLE = "text/turtle";
	public static final String CT_TEXT_TRIG = "text/trig";*/

	// HTTP Headers
	public static final String HDR_ALLOW = "Allow";
	public static final String HDR_ACCEPT_PATCH = "Accept-Patch";
	public static final String HDR_ACCEPT_POST = "Accept-Post";
	public static final String HDR_ETAG = "ETag";
	public static final String HDR_LINK = "Link";
	public static final String HDR_SLUG = "Slug";
	public static final String HDR_PREFER = "Prefer";
	public static final String HDR_PREFERENCE_APPLIED = "Preference-Applied";

	// Link relations
	public static final String LINK_PARAM_ANCHOR = "anchor";
	public static final String LINK_REL_TYPE = "type";
	public static final String LINK_REL_DESCRIBEDBY = "describedby";
	public static final String LINK_REL_CONSTRAINEDBY = nsName("constrainedBy");
	
	// LDP-CoAP Link relations
	public static final String LINK_LDP = "ldp";
	public static final String LINK_LDP_PREF_INCLUDE = "ldp-incl";
	public static final String LINK_LDP_PREF_OMIT = "ldp-omit";

	// RDF Property names both namespace and local
	public static final String PROP_LNAME_MEMBERSHIP_RESOURCE = "membershipResource";
	public static final String PROP_MEMBERSHIP_RESOURCE = nsName(PROP_LNAME_MEMBERSHIP_RESOURCE);
	public static final String PROP_LNAME_HAS_MEMBER_RELATION = "hasMemberRelation";
	public static final String PROP_HAS_MEMBER_RELATION = nsName(PROP_LNAME_HAS_MEMBER_RELATION);
	public static final String PROP_LNAME_IS_MEMBER_OF_RELATION = "isMemberOfRelation";
	public static final String PROP_IS_MEMBER_OF_RELATION = nsName(PROP_LNAME_IS_MEMBER_OF_RELATION);
	public static final String PROP_LNAME_MEMBER = "member";
	public static final String PROP_MEMBER = nsName(PROP_LNAME_MEMBER);
	public static final String PROP_LNAME_CONTAINS = "contains";
	public static final String PROP_CONTAINS = nsName(PROP_LNAME_CONTAINS);
	public static final String PROP_LNAME_PAGEOF = "pageOf";
	public static final String PROP_PAGEOF = nsName(PROP_LNAME_PAGEOF);
	public static final String PROP_LNAME_NEXTPAGE = "nextPage";
	public static final String PROP_NEXTPAGE = nsName(PROP_LNAME_NEXTPAGE);
	public static final String PROP_LNAME_CONTAINER_SORT_PREDICATE = "containerSortPredicate";
	public static final String PROP_CONTAINER_SORT_PREDICATE = nsName(PROP_LNAME_CONTAINER_SORT_PREDICATE);
	public static final String PROP_LNAME_INSERTED_CONTENT_RELATION = "insertedContentRelation";
	public static final String PROP_INSERTED_CONTENT_RELATION= nsName(PROP_LNAME_INSERTED_CONTENT_RELATION);
	
	// RDF Classes both namespace and local
	public static final String CLASS_LNAME_PAGE = "Page";
	public static final String CLASS_PAGE = nsName(CLASS_LNAME_PAGE);
	public static final String CLASS_LNAME_CONTAINER = "Container";
	public static final String CLASS_CONTAINER = nsName(CLASS_LNAME_CONTAINER);
	public static final String CLASS_LNAME_BASIC_CONTAINER = "BasicContainer";
	public static final String CLASS_BASIC_CONTAINER = nsName(CLASS_LNAME_BASIC_CONTAINER);
	public static final String CLASS_LNAME_DIRECT_CONTAINER = "DirectContainer";
	public static final String CLASS_DIRECT_CONTAINER = nsName(CLASS_LNAME_DIRECT_CONTAINER);
	public static final String CLASS_LNAME_INDIRECT_CONTAINER = "IndirectContainer";
	public static final String CLASS_INDIRECT_CONTAINER = nsName(CLASS_LNAME_INDIRECT_CONTAINER);
	public static final String CLASS_LNAME_RESOURCE = "Resource";
	public static final String CLASS_RESOURCE = nsName(CLASS_LNAME_RESOURCE);
	public static final String CLASS_LNAME_RDFSOURCE = "RDFSource";
	public static final String CLASS_RDFSOURCE = nsName(CLASS_LNAME_RDFSOURCE);
	public static final String CLASS_LNAME_NONRDFSOURCE = "NonRDFSource";
	public static final String CLASS_NONRDFSOURCE = nsName(CLASS_LNAME_NONRDFSOURCE);

	// Only container types that resource can be for, doesn't include #Container
	public static final String [] CONTAINER_TYPES = {CLASS_BASIC_CONTAINER, CLASS_DIRECT_CONTAINER, CLASS_INDIRECT_CONTAINER};

	// Prefer header preferences
	public static final String PREFER_RETURN_REPRESENTATION = "return=representation";
	public static final String PREFER_INCLUDE = "include";
	public static final String PREFER_OMIT = "omit";
	public static final String PREFER_CONTAINMENT = nsName("PreferContainment");
	public static final String PREFER_MEMBERSHIP = nsName("PreferMembership");
	public static final String PREFER_MINIMAL_CONTAINER = nsName("PreferMinimalContainer");

	/**
	 * Deprecated in LDP, but still supported by this reference implementation. The
	 * equivalent term that should be used instead is
	 * {@link #PREFER_MINIMAL_CONTAINER}.
	 */
	public static final String DEPRECATED_PREFER_EMPTY_CONTAINER = nsName("PreferEmptyContainer");

	public static String nsName(String local) {
		return NS + local;
	}
	
	/**
	 * The enumeration of request codes: GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS.
	 */
	public enum Code {
		
		/** The GET code. */
		GET(1),

		/** The POST code. */
		POST(2),
		
		/** The PUT code. */
		PUT(3),
		
		/** The DELETE code. */
		DELETE(4),
		
		/** The PATCH code. */
		PATCH(5),
		
		/** The HEAD code. */
		HEAD(6),
		
		/** The OPTIONS code. */
		OPTIONS(7);
		
		public final int value;
		
		/**
		 * Instantiates a new code with the specified code value.
		 *
		 * @param value the integer value of the code
		 */
		Code(int value) {
			this.value = value;
		}
		
		/**
		 * Converts the specified integer value to a request code.
		 *
		 * @param value the integer value
		 * @return the request code
		 * @throws IllegalArgumentException if the integer value is unrecognized
		 */
		public static Code valueOf(int value) {
			switch (value) {
				case 1: return GET;
				case 2: return POST;
				case 3: return PUT;
				case 4: return DELETE;
				case 5: return PATCH;
				case 6: return HEAD;
				case 7: return OPTIONS;
				default: throw new IllegalArgumentException("Unknwon LDP-CoAP request code "+value);
			}
		}
		
		public String toString() {
			switch (value) {
				case 1: return "GET";
				case 2: return "POST";
				case 3: return "PUT";
				case 4: return "DELETE";
				case 5: return "PATCH";
				case 6: return "HEAD";
				case 7: return "OPTIONS";
				default: throw new IllegalArgumentException("Unknwon LDP-CoAP request code "+value);
			}
		}
	}
}
