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

package corba.server;

import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

public class Bank_impl extends BankPOA {
    
    private ORB orb_;
    private POA poa_;

    private Map<String, Account> accountList;
    
    Bank_impl(ORB orb, POA poa) {
        orb_ = orb;
        poa_ = poa;

        accountList = new HashMap<String, Account>();
    }

    public Account create_account(String account_name) {
        System.out.println("[Bank] Called create_account( " + account_name + " )...");
        System.out.println();
        Account_impl accountImpl = new Account_impl(poa_);
        Account account = accountImpl._this(orb_);
        accountList.put(account_name, account);
        return account;
    }

    public org.omg.CORBA.Object create_epr_account(String account_name) {
        System.out.println("[Bank] Called create_epr_account( " + account_name + ")...");
        System.out.println();
        Account_impl accountImpl = new Account_impl(poa_);
        Account account = accountImpl._this(orb_);
        accountList.put(account_name, account);
        return account;
    }

    public Account get_account(String account_name) {
        System.out.println("[Bank] Called get_account( " + account_name + ")...");
        System.out.println();
        return accountList.get(account_name);
    }

    public org.omg.CORBA.Object get_epr_account(String account_name) {
        System.out.println("[Bank] Called get_epr_account( " + account_name + ")...");
        System.out.println();
        return accountList.get(account_name);
    }

    // TODO: What is the correct implementation for this operation?
    public org.omg.CORBA.Object get_account_epr_with_no_use_attribute(String account_name) {
        System.out.println("[Bank] Called get_epr_with_no_use_attribute( " + account_name + " )...");
        System.out.println();
        return null;
    }

    // TODO: What is the correct implementation for this operation?
    public void find_account(org.omg.CORBA.AnyHolder account_details) {
        System.out.println("[Bank] Called find_account (account_details)...");
        System.out.println();
    }

    public void remove_account(String account_name) {
        System.out.println("[Bank] Called remove_account ( " + account_name + ")...");
        System.out.println();
        accountList.remove(account_name);
    }
    
    public POA _default_POA() {
        return poa_;
    }
}
