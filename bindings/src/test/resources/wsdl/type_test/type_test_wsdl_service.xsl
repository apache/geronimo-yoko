<?xml version="1.0"?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements. See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership. The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License. You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied. See the License for the
  specific language governing permissions and limitations
  under the License.
-->
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xalan="http://xml.apache.org/xslt"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema"
    xmlns:wsdl="http://schemas.xmlsoap.org/wsdl/"
    xmlns:wsse="http://schemas.xmlsoap.org/ws/2003/06/secext"
    xmlns:itst="http://tests.iona.com/ittests">

  <xsl:output method="xml" indent="yes" xalan:indent-amount="4"/>
  <xsl:strip-space elements="*"/>

  <!-- 0 - root wsdl definitions -->
  <xsl:template match="/wsdl:definitions">
      <definitions
          xmlns="http://schemas.xmlsoap.org/wsdl/"
          xmlns:corba="http://schemas.apache.org/yoko/bindings/corba" 
          xmlns:tns="http://apache.org/type_test/corba"
          targetNamespace="http://apache.org/type_test/corba"
          name="type_test">
          <xsl:copy-of select="*"/>
          <service name="CORBAService">
              <port name="CORBAPort" binding="tns:TypeTestCORBABinding">
                  <corba:address location="corbaloc::localhost:40012/type_test"/>
              </port>
          </service>
      </definitions>
  </xsl:template>

</xsl:stylesheet>

