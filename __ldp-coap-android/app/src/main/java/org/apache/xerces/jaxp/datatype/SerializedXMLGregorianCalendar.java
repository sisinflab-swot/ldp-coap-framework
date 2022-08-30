/***********************************************************************************************************************
 *
 * blueBill Mobile - Android - open source birding
 * Copyright (C) 2009-2011 by Tidalwave s.a.s. (http://www.tidalwave.it)
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************
 *
 * WWW: http://bluebill.tidalwave.it/mobile
 * SCM: https://java.net/hg/bluebill-mobile~android-src
 *
 **********************************************************************************************************************/
package org.apache.xerces.jaxp.datatype;

import java.io.ObjectStreamException;
import java.io.Serializable;

/** 
 *
 * Modified by Fabrizio Giudici (fabrizio dot giudici at tidalwave dot it), just to work in a non java.* set of
 * packages to provide a patch to missing classes in Android 1.5+.
 *
 * <p>Serialized form of <code>javax.xml.datatype.XMLGregorianCalendar</code>.</p>
 * 
 * @author Michael Glavassevich, IBM
 * @version $Id$
 */
final class SerializedXMLGregorianCalendar implements Serializable {

    private static final long serialVersionUID = -7752272381890705397L;
    private final String lexicalValue;
    
    public SerializedXMLGregorianCalendar(String lexicalValue) {
        this.lexicalValue = lexicalValue;
    }
    
    private Object readResolve() throws ObjectStreamException {
        return new DatatypeFactoryImpl().newXMLGregorianCalendar(lexicalValue);
    }
}
