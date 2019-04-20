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
 package org.apache.yoko.orb.IOP;

 import org.omg.CORBA.BAD_INV_ORDER;
 import org.omg.IOP.ServiceContext;

 import java.util.Map;

 import static java.util.Arrays.copyOf;
 import static org.apache.yoko.orb.OB.MinorCodes.MinorServiceContextExists;
 import static org.apache.yoko.orb.OB.MinorCodes.describeBadInvOrder;
 import static org.omg.CORBA.CompletionStatus.COMPLETED_NO;

 public final class MutableServiceContexts {
     private final Map<Integer, ServiceContext> contexts;

     protected MutableServiceContexts(Map<Integer, ServiceContext> contexts) {
         this.contexts = contexts;
     }

     private static ServiceContext copy(ServiceContext sc) {
         if (sc == null) return null;
         ServiceContext result = new ServiceContext();
         result.context_id = sc.context_id;
         result.context_data = copyOf(sc.context_data, sc.context_data.length);
         return result;
     }

     public ServiceContext get(int id) {
         return copy(contexts.get(id));
     }

     public void add(ServiceContext context, boolean okToReplace) {
         if (okToReplace) add(context);
         else if (!addIfAbsent(context)) throw newBadInvOrder(MinorServiceContextExists, context.context_id);
     }

     private boolean addIfAbsent(ServiceContext context) {
         return null == contexts.putIfAbsent(context.context_id, copy(context));
     }

     private static BAD_INV_ORDER newBadInvOrder(int minorCode, int id) {
         return new BAD_INV_ORDER(describeBadInvOrder(minorCode) + ": " + id, minorCode, COMPLETED_NO);
     }

     public void add(ServiceContext context) {
         contexts.put(context.context_id, copy(context));
     }
 }
