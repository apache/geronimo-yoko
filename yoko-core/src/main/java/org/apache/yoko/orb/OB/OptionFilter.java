/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
*  contributor license agreements.  See the NOTICE file distributed with
*  this work for additional information regarding copyright ownership.
*  The ASF licenses this file to You under the Apache License, Version 2.0
*  (the "License"); you may not use this file except in compliance with
*  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.yoko.orb.OB;

import org.apache.yoko.orb.OB.Logger;

//
// This class maintains a list of options starting with a particular prefix.
//

public final class OptionFilter {
    //
    // Prefix for error messages
    //
    private String errorPrefix_;

    //
    // Prefix for the options handled by this filter
    //
    private String optionPrefix_;

    //
    // List with options and their number of arguments
    //
    private java.util.Hashtable argTable_ = new java.util.Hashtable();

    //
    // An option
    //
    public class Option {
        public String name;

        public String[] value;

        public Option(String n, String[] v) {
            name = n;
            value = v;
        }
    }

    //
    // Constructor
    //
    public OptionFilter(String errorPrefix, String optionPrefix) {
        errorPrefix_ = errorPrefix;
        optionPrefix_ = optionPrefix;
    }

    //
    // Add option string and the number of arguments for this option
    //
    public void add(String option, int nrOfArgs) {
        argTable_.put(option, new Integer(nrOfArgs));
    }

    //
    // Parse option list by extracting known options.
    // Check if the number of arguments is sufficient for each option.
    //
    public Option[] parse(Logger logger, String[] args) {
        java.util.Vector options = new java.util.Vector();

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(optionPrefix_)) {
                String option = args[i].substring(optionPrefix_.length());
                Integer nrOfArgs = (Integer) argTable_.get(option);
                if (nrOfArgs != null) {
                    int n = nrOfArgs.intValue();
                    String[] value = new String[n];

                    if (i + n >= args.length) {
                        String err = errorPrefix_ + ": argument expected for `"
                                + optionPrefix_ + option + "'";
                        logger.error(err);
                        throw new org.omg.CORBA.INITIALIZE(err);
                    }

                    for (int j = 0; j < n; j++)
                        value[j] = args[++i];

                    options.addElement(new Option(option, value));

                    continue;
                } else
                    logger.warning(errorPrefix_ + ": unknown option: `"
                            + optionPrefix_ + option + "'");
            }
        }

        Option[] result = new Option[options.size()];
        options.copyInto(result);
        return result;
    }

    //
    // Filter known options and their arguments
    //
    public String[] filter(String[] args) {
        java.util.Vector unknown = new java.util.Vector(args.length);

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith(optionPrefix_)) {
                Integer nrOfArgs = (Integer) argTable_.get(args[i]
                        .substring(optionPrefix_.length()));
                if (nrOfArgs != null)
                    i += nrOfArgs.intValue();

                continue;
            }

            unknown.addElement(args[i]);
        }

        String[] rem = new String[unknown.size()];
        unknown.copyInto(rem);

        return rem;
    }
}
