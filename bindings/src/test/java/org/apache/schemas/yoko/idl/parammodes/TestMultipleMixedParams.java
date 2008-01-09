/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/

package org.apache.schemas.yoko.idl.parammodes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for testMultipleMixedParams element declaration.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;element name="testMultipleMixedParams">
 *   &lt;complexType>
 *     &lt;complexContent>
 *       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *         &lt;sequence>
 *           &lt;element name="p1" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *           &lt;element name="p3" type="{http://www.w3.org/2001/XMLSchema}short"/>
 *         &lt;/sequence>
 *       &lt;/restriction>
 *     &lt;/complexContent>
 *   &lt;/complexType>
 * &lt;/element>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "p1",
    "p3"
})
@XmlRootElement(name = "testMultipleMixedParams")
public class TestMultipleMixedParams {

    protected short p1;
    protected short p3;

    /**
     * Gets the value of the p1 property.
     * 
     */
    public short getP1() {
        return p1;
    }

    /**
     * Sets the value of the p1 property.
     * 
     */
    public void setP1(short value) {
        this.p1 = value;
    }

    /**
     * Gets the value of the p3 property.
     * 
     */
    public short getP3() {
        return p3;
    }

    /**
     * Sets the value of the p3 property.
     * 
     */
    public void setP3(short value) {
        this.p3 = value;
    }

}
