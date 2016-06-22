package it.poliba.sisinflab.rdf.vocabulary;

import org.eclipse.rdf4j.model.URI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.ValueFactoryImpl;

/**
 * Semantic Sensor Network Ontology.
 * <p>
 * Please report any errors to the Semantic Sensor Network Incubator
 * Activity via the public W3C list public-xg-ssn@w3.org.
 * <p>
 * Namespace http://purl.oclc.org/NET/ssnx/ssn.
 * Prefix: {@code <http://purl.oclc.org/NET/ssnx/ssn>}
 *
 */
public class SSN_XG {

	/** {@code http://purl.oclc.org/NET/ssnx/ssn} **/
	public static final String NAMESPACE = "http://purl.oclc.org/NET/ssnx/ssn";

	/** {@code http://purl.oclc.org/net/ssnx/ssn} **/
	public static final String PREFIX = "ssn";

	/**
	 * Accuracy
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Accuracy}.
	 * <p>
	 * The closeness of agreement between the value of an observation and the
	 * true value of the observed quality.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Accuracy">#Accuracy</a>
	 */
	public static final URI Accuracy;

	/**
	 * attached system
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#attachedSystem}.
	 * <p>
	 * Relation between a Platform and any Systems (e.g., Sensors) that are
	 * attached to the Platform.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#attachedSystem">#attachedSystem</a>
	 */
	public static final URI attachedSystem;

	/**
	 * Battery Lifetime
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#BatteryLifetime}.
	 * <p>
	 * Total useful life of a battery.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#BatteryLifetime">#BatteryLifetime</a>
	 */
	public static final URI BatteryLifetime;

	/**
	 * Condition
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Condition}.
	 * <p>
	 * Used to specify ranges for qualities that act as conditions on a
	 * system/sensor's operation. For example, wind speed of 10-60m/s is
	 * expressed as a condition linking a quality, wind speed, a unit of
	 * measurement, metres per second, and a set of values, 10-60, and may be
	 * used as the condition on a MeasurementProperty, for example, to state
	 * that a sensor has a particular accuracy in that condition.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Condition">#Condition</a>
	 */
	public static final URI Condition;

	/**
	 * deployed on platform
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#deployedOnPlatform}.
	 * <p>
	 * Relation between a deployment and the platform on which the system was
	 * deployed.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#deployedOnPlatform">#deployedOnPlatform</a>
	 */
	public static final URI deployedOnPlatform;

	/**
	 * deployed system
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#deployedSystem}.
	 * <p>
	 * Relation between a deployment and the deployed system.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#deployedSystem">#deployedSystem</a>
	 */
	public static final URI deployedSystem;

	/**
	 * Deployment
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Deployment}.
	 * <p>
	 * The ongoing Process of Entities (for the purposes of this ontology,
	 * mainly sensors) deployed for a particular purpose. For example, a
	 * particular Sensor deployed on a Platform, or a whole network of
	 * Sensors deployed for an observation campaign. The deployment may have
	 * sub processes, such as installation, maintenance, addition, and
	 * decomissioning and removal.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Deployment">#Deployment</a>
	 */
	public static final URI Deployment;

	/**
	 * deployment process part
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#deploymentProcessPart}.
	 * <p>
	 * Has part relation between a deployment process and its constituent
	 * processes.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#deploymentProcessPart">#deploymentProcessPart</a>
	 */
	public static final URI deploymentProcessPart;

	/**
	 * Deployment-related Process
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#DeploymentRelatedProcess}.
	 * <p>
	 * Place to group all the various Processes related to Deployment. For
	 * example, as well as Deplyment, installation, maintenance, deployment
	 * of further sensors and the like would all be classified under
	 * DeploymentRelatedProcess.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#DeploymentRelatedProcess">#DeploymentRelatedProcess</a>
	 */
	public static final URI DeploymentRelatedProcess;

	/**
	 * detection limit
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#DetectionLimit}.
	 * <p>
	 * An observed value for which the probability of falsely claiming the
	 * absence of a component in a material is Î², given a probability Î± of
	 * falsely claiming its presence.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#DetectionLimit">#DetectionLimit</a>
	 */
	public static final URI DetectionLimit;

	/**
	 * detects
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#detects}.
	 * <p>
	 * A relation from a sensor to the Stimulus that the sensor can detect.
	 * The Stimulus itself will be serving as a proxy for (see isProxyOf)
	 * some observable property.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#detects">#detects</a>
	 */
	public static final URI detects;

	/**
	 * Device
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Device}.
	 * <p>
	 * A device is a physical piece of technology - a system in a box.
	 * Devices may of course be built of smaller devices and software
	 * components (i.e. systems have components).
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Device">#Device</a>
	 */
	public static final URI Device;

	/**
	 * Drift
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Drift}.
	 * <p>
	 * A, continuous or incremental, change in the reported values of
	 * observations over time for an unchanging quality.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Drift">#Drift</a>
	 */
	public static final URI Drift;

	/**
	 * end time
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#endTime}.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#endTime">#endTime</a>
	 */
	public static final URI endTime;

	/**
	 * feature of interest
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest}.
	 * <p>
	 * A relation between an observation and the entity whose quality was
	 * observed. For example, in an observation of the weight of a person,
	 * the feature of interest is the person and the quality is weight.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest">#featureOfInterest</a>
	 */
	public static final URI featureOfInterest;

	/**
	 * Feature of Interest
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#FeatureOfInterest}.
	 * <p>
	 * A feature is an abstraction of real world phenomena (thing, person,
	 * event, etc).
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#FeatureOfInterest">#FeatureOfInterest</a>
	 */
	public static final URI FeatureOfInterest;

	/**
	 * for property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#forProperty}.
	 * <p>
	 * A relation between some aspect of a sensing entity and a property. For
	 * example, from a sensor to the properties it can observe, or from a
	 * deployment to the properties it was installed to observe. Also from a
	 * measurement capability to the property the capability is described
	 * for. (Used in conjunction with ofFeature).
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#forProperty">#forProperty</a>
	 */
	public static final URI forProperty;

	/**
	 * Frequency
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Frequency}.
	 * <p>
	 * The smallest possible time between one observation and the next.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Frequency">#Frequency</a>
	 */
	public static final URI Frequency;

	/**
	 * has deployment
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasDeployment}.
	 * <p>
	 * Relation between a System and a Deployment, recording that the
	 * System/Sensor was deployed in that Deployment.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasDeployment">#hasDeployment</a>
	 */
	public static final URI hasDeployment;

	/**
	 * has input
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasInput}.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasInput">#hasInput</a>
	 */
	public static final URI hasInput;

	/**
	 * has measurement  capability
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementCapability}.
	 * <p>
	 * Relation from a Sensor to a MeasurementCapability describing the
	 * measurement properties of the sensor.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementCapability">#hasMeasurementCapability</a>
	 */
	public static final URI hasMeasurementCapability;

	/**
	 * has measurement property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementProperty}.
	 * <p>
	 * Relation from a MeasurementCapability to a MeasurementProperty. For
	 * example, to an accuracy (see notes at MeasurementCapability).
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasMeasurementProperty">#hasMeasurementProperty</a>
	 */
	public static final URI hasMeasurementProperty;

	/**
	 * has operating property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasOperatingProperty}.
	 * <p>
	 * Relation from an OperatingRange to a Property. For example, to a
	 * battery lifetime.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasOperatingProperty">#hasOperatingProperty</a>
	 */
	public static final URI hasOperatingProperty;

	/**
	 * has operating range
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasOperatingRange}.
	 * <p>
	 * Relation from a System to an OperatingRange describing the normal
	 * operating environment of the System.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasOperatingRange">#hasOperatingRange</a>
	 */
	public static final URI hasOperatingRange;

	/**
	 * has output
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasOutput}.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasOutput">#hasOutput</a>
	 */
	public static final URI hasOutput;

	/**
	 * has property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasProperty}.
	 * <p>
	 * A relation between a FeatureOfInterest and a Property of that feature.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasProperty">#hasProperty</a>
	 */
	public static final URI hasProperty;

	/**
	 * has subsystem
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasSubSystem}.
	 * <p>
	 * Haspart relation between a system and its parts.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasSubSystem">#hasSubSystem</a>
	 */
	public static final URI hasSubSystem;

	/**
	 * has survival property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasSurvivalProperty}.
	 * <p>
	 * Relation from a SurvivalRange to a Property describing the survial
	 * range of a system. For example, to the temperature extreme that a
	 * system can withstand before being considered damaged.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasSurvivalProperty">#hasSurvivalProperty</a>
	 */
	public static final URI hasSurvivalProperty;

	/**
	 * has survival range
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasSurvivalRange}.
	 * <p>
	 * A Relation from a System to a SurvivalRange.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasSurvivalRange">#hasSurvivalRange</a>
	 */
	public static final URI hasSurvivalRange;

	/**
	 * has value
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#hasValue}.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#hasValue">#hasValue</a>
	 */
	public static final URI hasValue;

	/**
	 * implemented by
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#implementedBy}.
	 * <p>
	 * A relation between the description of an algorithm, procedure or
	 * method and an entity that implements that method in some executable
	 * way. For example, between a scientific measuring method and a sensor
	 * the senses via that method.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#implementedBy">#implementedBy</a>
	 */
	public static final URI implementedBy;

	/**
	 * implements
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#implements}.
	 * <p>
	 * A relation between an entity that implements a method in some
	 * executable way and the description of an algorithm, procedure or
	 * method. For example, between a Sensor and the scientific measuring
	 * method that the Sensor uses to observe a Property.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#implements">#implements</a>
	 */
	public static final URI _implements;

	/**
	 * in condition
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#inCondition}.
	 * <p>
	 * Describes the prevailing environmental conditions for
	 * MeasurementCapabilites, OperatingConditions and SurvivalRanges. Used
	 * for example to say that a sensor has a particular accuracy in
	 * particular conditions. (see also MeasurementCapability)
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#inCondition">#inCondition</a>
	 */
	public static final URI inCondition;

	/**
	 * in deployment
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#inDeployment}.
	 * <p>
	 * Relation between a Platform and a Deployment, recording that the
	 * object was used as a platform for a system/sensor for a particular
	 * deployment: as in this PhysicalObject is acting as a Platform
	 * inDeployment Deployment.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#inDeployment">#inDeployment</a>
	 */
	public static final URI inDeployment;

	/**
	 * Input
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Input}.
	 * <p>
	 * Any information that is provided to a process for its use [MMI OntDev]
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Input">#Input</a>
	 */
	public static final URI Input;

	/**
	 * is produced by
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#isProducedBy}.
	 * <p>
	 * Relation between a producer and a produced entity: for example,
	 * between a sensor and the produced output.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#isProducedBy">#isProducedBy</a>
	 */
	public static final URI isProducedBy;

	/**
	 * is property of
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#isPropertyOf}.
	 * <p>
	 * Relation between a FeatureOfInterest and a Property (a Quality
	 * observable by a sensor) of that feature.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#isPropertyOf">#isPropertyOf</a>
	 */
	public static final URI isPropertyOf;

	/**
	 * isProxyFor
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#isProxyFor}.
	 * <p>
	 * A relation from a Stimulus to the Property that the Stimulus is
	 * serving as a proxy for. For example, the expansion of the quicksilver
	 * is a stimulus that serves as a proxy for temperature, or an increase
	 * or decrease in the spinning of cups on a wind sensor is serving as a
	 * proxy for wind speed.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#isProxyFor">#isProxyFor</a>
	 */
	public static final URI isProxyFor;

	/**
	 * Latency
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Latency}.
	 * <p>
	 * The time between a request for an observation and the sensor providing
	 * a result.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Latency">#Latency</a>
	 */
	public static final URI Latency;

	/**
	 * made observation
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#madeObservation}.
	 * <p>
	 * Relation between a Sensor and Observations it has made.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#madeObservation">#madeObservation</a>
	 */
	public static final URI madeObservation;

	/**
	 * Maintenance Schedule
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#MaintenanceSchedule}.
	 * <p>
	 * Schedule of maintenance for a system/sensor in the specified
	 * conditions.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#MaintenanceSchedule">#MaintenanceSchedule</a>
	 */
	public static final URI MaintenanceSchedule;

	/**
	 * Measurement Capability
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#MeasurementCapability}.
	 * <p>
	 * Collects together measurement properties (accuracy, range, precision,
	 * etc) and the environmental conditions in which those properties hold,
	 * representing a specification of a sensor's capability in those
	 * conditions. The conditions specified here are those that affect the
	 * measurement properties, while those in OperatingRange represent the
	 * sensor's standard operating conditions, including conditions that
	 * don't affect the observations.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#MeasurementCapability">#MeasurementCapability</a>
	 */
	public static final URI MeasurementCapability;

	/**
	 * Measurement  Property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#MeasurementProperty}.
	 * <p>
	 * An identifiable and observable characteristic of a sensor's
	 * observations or ability to make observations.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#MeasurementProperty">#MeasurementProperty</a>
	 */
	public static final URI MeasurementProperty;

	/**
	 * Measurement  Range
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#MeasurementRange}.
	 * <p>
	 * The set of values that the sensor can return as the result of an
	 * observation under the defined conditions with the defined measurement
	 * properties. (If no conditions are specified or the conditions do not
	 * specify a range for the observed qualities, the measurement range is
	 * to be taken as the condition for the observed qualities.)
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#MeasurementRange">#MeasurementRange</a>
	 */
	public static final URI MeasurementRange;

	/**
	 * Observation
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Observation}.
	 * <p>
	 * An Observation is a Situation in which a Sensing method has been used
	 * to estimate or calculate a value of a Property of a FeatureOfInterest.
	 * Links to Sensing and Sensor describe what made the Observation and
	 * how; links to Property and Feature detail what was sensed; the result
	 * is the output of a Sensor; other metadata details times etc.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Observation">#Observation</a>
	 */
	public static final URI Observation;

	/**
	 * observation result
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#observationResult}.
	 * <p>
	 * Relation linking an Observation (i.e., a description of the context,
	 * the Situation, in which the observatioin was made) and a Result, which
	 * contains a value representing the value associated with the observed
	 * Property.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#observationResult">#observationResult</a>
	 */
	public static final URI observationResult;

	/**
	 * observation result time
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#observationResultTime}.
	 * <p>
	 * The result time is the time when the procedure associated with the
	 * observation act was applied.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#observationResultTime">#observationResultTime</a>
	 */
	public static final URI observationResultTime;

	/**
	 * observation sampling time
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#observationSamplingTime}.
	 * <p>
	 * Rebadged as phenomenon time in [O&M]. The phenomenon time shall
	 * describe the time that the result applies to the property of the
	 * feature-of-interest. This is often the time of interaction by a
	 * sampling procedure or observation procedure with a real-world feature.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#observationSamplingTime">#observationSamplingTime</a>
	 */
	public static final URI observationSamplingTime;

	/**
	 * Observation Value
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#ObservationValue}.
	 * <p>
	 * The value of the result of an Observation. An Observation has a result
	 * which is the output of some sensor, the result is an information
	 * object that encodes some value for a Feature.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#ObservationValue">#ObservationValue</a>
	 */
	public static final URI ObservationValue;

	/**
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#observedBy}.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#observedBy">#observedBy</a>
	 */
	public static final URI observedBy;

	/**
	 * observed property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#observedProperty}.
	 * <p>
	 * Relation linking an Observation to the Property that was observed. The
	 * observedProperty should be a Property (hasProperty) of the
	 * FeatureOfInterest (linked by featureOfInterest) of this observation.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#observedProperty">#observedProperty</a>
	 */
	public static final URI observedProperty;

	/**
	 * observes
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#observes}.
	 * <p>
	 * Relation between a Sensor and a Property that the sensor can observe.
	 * Note that, given the DUL modelling of Qualities, a sensor defined with
	 * 'observes only Windspeed' technically links the sensor to particular
	 * instances of Windspeed, not to the concept itself - OWL can't express
	 * concept-concept relations, only individual-individual. The property
	 * composition ensures that if an observation is made of a particular
	 * quality then one can infer that the sensor observes that quality.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#observes">#observes</a>
	 */
	public static final URI observes;

	/**
	 * of feature
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#ofFeature}.
	 * <p>
	 * A relation between some aspect of a sensing entity and a feature. For
	 * example, from a sensor to the features it can observe properties of,
	 * or from a deployment to the features it was installed to observe. Also
	 * from a measurement capability to the feature the capability is
	 * described for. (Used in conjunction with forProperty).
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#ofFeature">#ofFeature</a>
	 */
	public static final URI ofFeature;

	/**
	 * on platform
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#onPlatform}.
	 * <p>
	 * Relation between a System (e.g., a Sensor) and a Platform. The
	 * relation locates the sensor relative to other described entities
	 * entities: i.e., the Sensor s1's location is Platform p1. More precise
	 * locations for sensors in space (relative to other entities, where
	 * attached to another entity, or in 3D space) are made using DOLCE's
	 * Regions (SpaceRegion).
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#onPlatform">#onPlatform</a>
	 */
	public static final URI onPlatform;

	/**
	 * Operating Power Range
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#OperatingPowerRange}.
	 * <p>
	 * Power range in which system/sensor is expected to operate.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#OperatingPowerRange">#OperatingPowerRange</a>
	 */
	public static final URI OperatingPowerRange;

	/**
	 * Operating Property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#OperatingProperty}.
	 * <p>
	 * An identifiable characteristic of the environmental and other
	 * conditions in which the sensor is intended to operate. May include
	 * power ranges, power sources, standard configurations, attachments and
	 * the like.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#OperatingProperty">#OperatingProperty</a>
	 */
	public static final URI OperatingProperty;

	/**
	 * Operating Range
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#OperatingRange}.
	 * <p>
	 * The environmental conditions and characteristics of a system/sensor's
	 * normal operating environment. Can be used to specify for example the
	 * standard environmental conditions in which the sensor is expected to
	 * operate (a Condition with no OperatingProperty), or how the
	 * environmental and other operating properties relate: i.e., that the
	 * maintenance schedule or power requirements differ according to the
	 * conditions.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#OperatingRange">#OperatingRange</a>
	 */
	public static final URI OperatingRange;

	/**
	 * Output
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Output}.
	 * <p>
	 * Any information that is reported from a process. [MMI OntDev]
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Output">#Output</a>
	 */
	public static final URI Output;

	/**
	 * Platform
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Platform}.
	 * <p>
	 * An Entity to which other Entities can be attached - particuarly
	 * Sensors and other Platforms. For example, a post might act as the
	 * Platform, a bouy might act as a Platform, or a fish might act as a
	 * Platform for an attached sensor.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Platform">#Platform</a>
	 */
	public static final URI Platform;

	/**
	 * Precision
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Precision}.
	 * <p>
	 * The closeness of agreement between replicate observations on an
	 * unchanged or similar quality value: i.e., a measure of a sensor's
	 * ability to consitently reproduce an observation.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Precision">#Precision</a>
	 */
	public static final URI Precision;

	/**
	 * Process
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Process}.
	 * <p>
	 * A process has an output and possibly inputs and, for a composite
	 * process, describes the temporal and dataflow dependencies and
	 * relationships amongst its parts. [SSN XG]
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Process">#Process</a>
	 */
	public static final URI Process;

	/**
	 * Property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Property}.
	 * <p>
	 * An observable Quality of an Event or Object. That is, not a quality of
	 * an abstract entity as is also allowed by DUL's Quality, but rather an
	 * aspect of an entity that is intrinsic to and cannot exist without the
	 * entity and is observable by a sensor.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Property">#Property</a>
	 */
	public static final URI Property;

	/**
	 * quality of observation
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#qualityOfObservation}.
	 * <p>
	 * Relation linking an Observation to the adjudged quality of the result.
	 * This is of course complimentary to the MeasurementCapability
	 * information recorded for the Sensor that made the Observation.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#qualityOfObservation">#qualityOfObservation</a>
	 */
	public static final URI qualityOfObservation;

	/**
	 * Resolution
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Resolution}.
	 * <p>
	 * The smallest difference in the value of a quality being observed that
	 * would result in perceptably different values of observation results.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Resolution">#Resolution</a>
	 */
	public static final URI Resolution;

	/**
	 * Response time
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#ResponseTime}.
	 * <p>
	 * The time between a (step) change inthe value of an observed quality
	 * and a sensor (possibly with specified error) 'settling' on an observed
	 * value.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#ResponseTime">#ResponseTime</a>
	 */
	public static final URI ResponseTime;

	/**
	 * Selectivity
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Selectivity}.
	 * <p>
	 * Selectivity is a property of a sensor whereby it provides observed
	 * values for one or more qualities such that the values of each quality
	 * are independent of other qualities in the phenomenon, body, or
	 * substance being investigated.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Selectivity">#Selectivity</a>
	 */
	public static final URI Selectivity;

	/**
	 * Sensing
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Sensing}.
	 * <p>
	 * Sensing is a process that results in the estimation, or calculation,
	 * of the value of a phenomenon.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Sensing">#Sensing</a>
	 */
	public static final URI Sensing;

	/**
	 * Sensing Device
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#SensingDevice}.
	 * <p>
	 * A sensing device is a device that implements sensing.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#SensingDevice">#SensingDevice</a>
	 */
	public static final URI SensingDevice;

	/**
	 * sensing method used
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#sensingMethodUsed}.
	 * <p>
	 * A (measurement) procedure is a detailed description of a measurement
	 * according to one or more measurement principles and to a given
	 * measurement method, based on a measurement model and including any
	 * calculation to obtain a measurement result [VIM 2.6]
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#sensingMethodUsed">#sensingMethodUsed</a>
	 */
	public static final URI sensingMethodUsed;

	/**
	 * Sensitivity
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Sensitivity}.
	 * <p>
	 * Sensitivity is the quotient of the change in a result of sensor and
	 * the corresponding change in a value of a quality being observed.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Sensitivity">#Sensitivity</a>
	 */
	public static final URI Sensitivity;

	/**
	 * Sensor
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Sensor}.
	 * <p>
	 * A sensor can do (implements) sensing: that is, a sensor is any entity
	 * that can follow a sensing method and thus observe some Property of a
	 * FeatureOfInterest. Sensors may be physical devices, computational
	 * methods, a laboratory setup with a person following a method, or any
	 * other thing that can follow a Sensing Method to observe a Property.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Sensor">#Sensor</a>
	 */
	public static final URI Sensor;

	/**
	 * Sensor Data Sheet
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#SensorDataSheet}.
	 * <p>
	 * A data sheet records properties of a sensor. A data sheet might
	 * describe for example the accuracy in various conditions, the power
	 * use, the types of connectors that the sensor has, etc. Generally a
	 * sensor's properties are recorded directly (with
	 * hasMeasurementCapability, for example), but the data sheet can be used
	 * for example to record the manufacturers specifications verses observed
	 * capabilites, or if more is known than the manufacturer specifies, etc.
	 * The data sheet is an information object about the sensor's properties,
	 * rather than a direct link to the actual properties themselves.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#SensorDataSheet">#SensorDataSheet</a>
	 */
	public static final URI SensorDataSheet;

	/**
	 * Sensor Input
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#SensorInput}.
	 * <p>
	 * An Event in the real world that 'triggers' the sensor. The properties
	 * associated to the stimulus may be different to eventual observed
	 * property. It is the event, not the object that triggers the sensor.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#SensorInput">#SensorInput</a>
	 */
	public static final URI SensorInput;

	/**
	 * Sensor Output
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#SensorOutput}.
	 * <p>
	 * A sensor outputs a piece of information (an observed value), the value
	 * itself being represented by an ObservationValue.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#SensorOutput">#SensorOutput</a>
	 */
	public static final URI SensorOutput;

	/**
	 * start time
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#startTime}.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#startTime">#startTime</a>
	 */
	public static final URI startTime;

	/**
	 * Stimulus
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#Stimulus}.
	 * <p>
	 * An Event in the real world that 'triggers' the sensor. The properties
	 * associated to the stimulus may be different to eventual observed
	 * property. It is the event, not the object that triggers the sensor.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#Stimulus">#Stimulus</a>
	 */
	public static final URI Stimulus;

	/**
	 * Survival Property
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#SurvivalProperty}.
	 * <p>
	 * An identifiable characteristic that represents the extent of the
	 * sensors useful life. Might include for example total battery life or
	 * number of recharges, or, for sensors that are used only a fixed number
	 * of times, the number of observations that can be made before the
	 * sensing capability is depleted.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#SurvivalProperty">#SurvivalProperty</a>
	 */
	public static final URI SurvivalProperty;

	/**
	 * Survival Range
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#SurvivalRange}.
	 * <p>
	 * The conditions a sensor can be exposed to without damage: i.e., the
	 * sensor continues to operate as defined using MeasurementCapability.
	 * If, however, the SurvivalRange is exceeded, the sensor is 'damaged'
	 * and MeasurementCapability specifications may no longer hold.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#SurvivalRange">#SurvivalRange</a>
	 */
	public static final URI SurvivalRange;

	/**
	 * System
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#System}.
	 * <p>
	 * System is a unit of abstraction for pieces of infrastructure (and we
	 * largely care that they are) for sensing. A system has components, its
	 * subsystems, which are other systems.
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#System">#System</a>
	 */
	public static final URI System;

	/**
	 * System Lifetime
	 * <p>
	 * {@code http://purl.oclc.org/NET/ssnx/ssn#SystemLifetime}.
	 * <p>
	 * Total useful life of a sensor/system (expressed as total life since
	 * manufacture, time in use, number of operations, etc.).
	 *
	 * @see <a href="http://purl.oclc.org/NET/ssnx/ssn#SystemLifetime">#SystemLifetime</a>
	 */
	public static final URI SystemLifetime;

	static {
		ValueFactory factory = ValueFactoryImpl.getInstance();

		Accuracy = factory.createURI(SSN_XG.NAMESPACE, "#Accuracy");
		attachedSystem = factory.createURI(SSN_XG.NAMESPACE, "#attachedSystem");
		BatteryLifetime = factory.createURI(SSN_XG.NAMESPACE, "#BatteryLifetime");
		Condition = factory.createURI(SSN_XG.NAMESPACE, "#Condition");
		deployedOnPlatform = factory.createURI(SSN_XG.NAMESPACE, "#deployedOnPlatform");
		deployedSystem = factory.createURI(SSN_XG.NAMESPACE, "#deployedSystem");
		Deployment = factory.createURI(SSN_XG.NAMESPACE, "#Deployment");
		deploymentProcessPart = factory.createURI(SSN_XG.NAMESPACE, "#deploymentProcessPart");
		DeploymentRelatedProcess = factory.createURI(SSN_XG.NAMESPACE, "#DeploymentRelatedProcess");
		DetectionLimit = factory.createURI(SSN_XG.NAMESPACE, "#DetectionLimit");
		detects = factory.createURI(SSN_XG.NAMESPACE, "#detects");
		Device = factory.createURI(SSN_XG.NAMESPACE, "#Device");
		Drift = factory.createURI(SSN_XG.NAMESPACE, "#Drift");
		endTime = factory.createURI(SSN_XG.NAMESPACE, "#endTime");
		featureOfInterest = factory.createURI(SSN_XG.NAMESPACE, "#featureOfInterest");
		FeatureOfInterest = factory.createURI(SSN_XG.NAMESPACE, "#FeatureOfInterest");
		forProperty = factory.createURI(SSN_XG.NAMESPACE, "#forProperty");
		Frequency = factory.createURI(SSN_XG.NAMESPACE, "#Frequency");
		hasDeployment = factory.createURI(SSN_XG.NAMESPACE, "#hasDeployment");
		hasInput = factory.createURI(SSN_XG.NAMESPACE, "#hasInput");
		hasMeasurementCapability = factory.createURI(SSN_XG.NAMESPACE, "#hasMeasurementCapability");
		hasMeasurementProperty = factory.createURI(SSN_XG.NAMESPACE, "#hasMeasurementProperty");
		hasOperatingProperty = factory.createURI(SSN_XG.NAMESPACE, "#hasOperatingProperty");
		hasOperatingRange = factory.createURI(SSN_XG.NAMESPACE, "#hasOperatingRange");
		hasOutput = factory.createURI(SSN_XG.NAMESPACE, "#hasOutput");
		hasProperty = factory.createURI(SSN_XG.NAMESPACE, "#hasProperty");
		hasSubSystem = factory.createURI(SSN_XG.NAMESPACE, "#hasSubSystem");
		hasSurvivalProperty = factory.createURI(SSN_XG.NAMESPACE, "#hasSurvivalProperty");
		hasSurvivalRange = factory.createURI(SSN_XG.NAMESPACE, "#hasSurvivalRange");
		hasValue = factory.createURI(SSN_XG.NAMESPACE, "#hasValue");
		implementedBy = factory.createURI(SSN_XG.NAMESPACE, "#implementedBy");
		_implements = factory.createURI(SSN_XG.NAMESPACE, "#implements");
		inCondition = factory.createURI(SSN_XG.NAMESPACE, "#inCondition");
		inDeployment = factory.createURI(SSN_XG.NAMESPACE, "#inDeployment");
		Input = factory.createURI(SSN_XG.NAMESPACE, "#Input");
		isProducedBy = factory.createURI(SSN_XG.NAMESPACE, "#isProducedBy");
		isPropertyOf = factory.createURI(SSN_XG.NAMESPACE, "#isPropertyOf");
		isProxyFor = factory.createURI(SSN_XG.NAMESPACE, "#isProxyFor");
		Latency = factory.createURI(SSN_XG.NAMESPACE, "#Latency");
		madeObservation = factory.createURI(SSN_XG.NAMESPACE, "#madeObservation");
		MaintenanceSchedule = factory.createURI(SSN_XG.NAMESPACE, "#MaintenanceSchedule");
		MeasurementCapability = factory.createURI(SSN_XG.NAMESPACE, "#MeasurementCapability");
		MeasurementProperty = factory.createURI(SSN_XG.NAMESPACE, "#MeasurementProperty");
		MeasurementRange = factory.createURI(SSN_XG.NAMESPACE, "#MeasurementRange");
		Observation = factory.createURI(SSN_XG.NAMESPACE, "#Observation");
		observationResult = factory.createURI(SSN_XG.NAMESPACE, "#observationResult");
		observationResultTime = factory.createURI(SSN_XG.NAMESPACE, "#observationResultTime");
		observationSamplingTime = factory.createURI(SSN_XG.NAMESPACE, "#observationSamplingTime");
		ObservationValue = factory.createURI(SSN_XG.NAMESPACE, "#ObservationValue");
		observedBy = factory.createURI(SSN_XG.NAMESPACE, "#observedBy");
		observedProperty = factory.createURI(SSN_XG.NAMESPACE, "#observedProperty");
		observes = factory.createURI(SSN_XG.NAMESPACE, "#observes");
		ofFeature = factory.createURI(SSN_XG.NAMESPACE, "#ofFeature");
		onPlatform = factory.createURI(SSN_XG.NAMESPACE, "#onPlatform");
		OperatingPowerRange = factory.createURI(SSN_XG.NAMESPACE, "#OperatingPowerRange");
		OperatingProperty = factory.createURI(SSN_XG.NAMESPACE, "#OperatingProperty");
		OperatingRange = factory.createURI(SSN_XG.NAMESPACE, "#OperatingRange");
		Output = factory.createURI(SSN_XG.NAMESPACE, "#Output");
		Platform = factory.createURI(SSN_XG.NAMESPACE, "#Platform");
		Precision = factory.createURI(SSN_XG.NAMESPACE, "#Precision");
		Process = factory.createURI(SSN_XG.NAMESPACE, "#Process");
		Property = factory.createURI(SSN_XG.NAMESPACE, "#Property");
		qualityOfObservation = factory.createURI(SSN_XG.NAMESPACE, "#qualityOfObservation");
		Resolution = factory.createURI(SSN_XG.NAMESPACE, "#Resolution");
		ResponseTime = factory.createURI(SSN_XG.NAMESPACE, "#ResponseTime");
		Selectivity = factory.createURI(SSN_XG.NAMESPACE, "#Selectivity");
		Sensing = factory.createURI(SSN_XG.NAMESPACE, "#Sensing");
		SensingDevice = factory.createURI(SSN_XG.NAMESPACE, "#SensingDevice");
		sensingMethodUsed = factory.createURI(SSN_XG.NAMESPACE, "#sensingMethodUsed");
		Sensitivity = factory.createURI(SSN_XG.NAMESPACE, "#Sensitivity");
		Sensor = factory.createURI(SSN_XG.NAMESPACE, "#Sensor");
		SensorDataSheet = factory.createURI(SSN_XG.NAMESPACE, "#SensorDataSheet");
		SensorInput = factory.createURI(SSN_XG.NAMESPACE, "#SensorInput");
		SensorOutput = factory.createURI(SSN_XG.NAMESPACE, "#SensorOutput");
		startTime = factory.createURI(SSN_XG.NAMESPACE, "#startTime");
		Stimulus = factory.createURI(SSN_XG.NAMESPACE, "#Stimulus");
		SurvivalProperty = factory.createURI(SSN_XG.NAMESPACE, "#SurvivalProperty");
		SurvivalRange = factory.createURI(SSN_XG.NAMESPACE, "#SurvivalRange");
		System = factory.createURI(SSN_XG.NAMESPACE, "#System");
		SystemLifetime = factory.createURI(SSN_XG.NAMESPACE, "#SystemLifetime");
	}

	private SSN_XG() {
		//static access only
	}

}
