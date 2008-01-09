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

package org.omg.CORBA;

//
// IDL:omg.org/CORBA/Container:1.0
//
public class _ContainerStub extends org.omg.CORBA.portable.ObjectImpl
                            implements Container
{
    private static final String[] _ob_ids_ =
    {
        "IDL:omg.org/CORBA/Container:1.0",
        "IDL:omg.org/CORBA/IRObject:1.0"
    };

    public String[]
    _ids()
    {
        return _ob_ids_;
    }

    final public static java.lang.Class _ob_opsClass = ContainerOperations.class;

    //
    // IDL:omg.org/CORBA/IRObject/def_kind:1.0
    //
    public DefinitionKind
    def_kind()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("_get_def_kind", true);
                    in = _invoke(out);
                    DefinitionKind _ob_r = DefinitionKindHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("def_kind", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.def_kind();
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/lookup:1.0
    //
    public Contained
    lookup(String _ob_a0)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("lookup", true);
                    ScopedNameHelper.write(out, _ob_a0);
                    in = _invoke(out);
                    Contained _ob_r = ContainedHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("lookup", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.lookup(_ob_a0);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/contents:1.0
    //
    public Contained[]
    contents(DefinitionKind _ob_a0,
             boolean _ob_a1)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("contents", true);
                    DefinitionKindHelper.write(out, _ob_a0);
                    out.write_boolean(_ob_a1);
                    in = _invoke(out);
                    Contained[] _ob_r = ContainedSeqHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("contents", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.contents(_ob_a0, _ob_a1);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/lookup_name:1.0
    //
    public Contained[]
    lookup_name(String _ob_a0,
                int _ob_a1,
                DefinitionKind _ob_a2,
                boolean _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("lookup_name", true);
                    IdentifierHelper.write(out, _ob_a0);
                    out.write_long(_ob_a1);
                    DefinitionKindHelper.write(out, _ob_a2);
                    out.write_boolean(_ob_a3);
                    in = _invoke(out);
                    Contained[] _ob_r = ContainedSeqHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("lookup_name", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.lookup_name(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/describe_contents:1.0
    //
    public org.omg.CORBA.ContainerPackage.Description[]
    describe_contents(DefinitionKind _ob_a0,
                      boolean _ob_a1,
                      int _ob_a2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("describe_contents", true);
                    DefinitionKindHelper.write(out, _ob_a0);
                    out.write_boolean(_ob_a1);
                    out.write_long(_ob_a2);
                    in = _invoke(out);
                    org.omg.CORBA.ContainerPackage.Description[] _ob_r = org.omg.CORBA.ContainerPackage.DescriptionSeqHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("describe_contents", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.describe_contents(_ob_a0, _ob_a1, _ob_a2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_module:1.0
    //
    public ModuleDef
    create_module(String _ob_a0,
                  String _ob_a1,
                  String _ob_a2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_module", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    in = _invoke(out);
                    ModuleDef _ob_r = ModuleDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_module", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_module(_ob_a0, _ob_a1, _ob_a2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_constant:1.0
    //
    public ConstantDef
    create_constant(String _ob_a0,
                    String _ob_a1,
                    String _ob_a2,
                    IDLType _ob_a3,
                    org.omg.CORBA.Any _ob_a4)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_constant", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    IDLTypeHelper.write(out, _ob_a3);
                    out.write_any(_ob_a4);
                    in = _invoke(out);
                    ConstantDef _ob_r = ConstantDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_constant", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_constant(_ob_a0, _ob_a1, _ob_a2, _ob_a3, _ob_a4);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_struct:1.0
    //
    public StructDef
    create_struct(String _ob_a0,
                  String _ob_a1,
                  String _ob_a2,
                  StructMember[] _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_struct", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    StructMemberSeqHelper.write(out, _ob_a3);
                    in = _invoke(out);
                    StructDef _ob_r = StructDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_struct", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_struct(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_union:1.0
    //
    public UnionDef
    create_union(String _ob_a0,
                 String _ob_a1,
                 String _ob_a2,
                 IDLType _ob_a3,
                 UnionMember[] _ob_a4)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_union", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    IDLTypeHelper.write(out, _ob_a3);
                    UnionMemberSeqHelper.write(out, _ob_a4);
                    in = _invoke(out);
                    UnionDef _ob_r = UnionDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_union", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_union(_ob_a0, _ob_a1, _ob_a2, _ob_a3, _ob_a4);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_enum:1.0
    //
    public EnumDef
    create_enum(String _ob_a0,
                String _ob_a1,
                String _ob_a2,
                String[] _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_enum", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    EnumMemberSeqHelper.write(out, _ob_a3);
                    in = _invoke(out);
                    EnumDef _ob_r = EnumDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_enum", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_enum(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_alias:1.0
    //
    public AliasDef
    create_alias(String _ob_a0,
                 String _ob_a1,
                 String _ob_a2,
                 IDLType _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_alias", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    IDLTypeHelper.write(out, _ob_a3);
                    in = _invoke(out);
                    AliasDef _ob_r = AliasDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_alias", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_alias(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_interface:1.0
    //
    public InterfaceDef
    create_interface(String _ob_a0,
                     String _ob_a1,
                     String _ob_a2,
                     InterfaceDef[] _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_interface", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    InterfaceDefSeqHelper.write(out, _ob_a3);
                    in = _invoke(out);
                    InterfaceDef _ob_r = InterfaceDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_interface", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_interface(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_abstract_interface:1.0
    //
    public AbstractInterfaceDef
    create_abstract_interface(String _ob_a0,
                              String _ob_a1,
                              String _ob_a2,
                              AbstractInterfaceDef[] _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_abstract_interface", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    AbstractInterfaceDefSeqHelper.write(out, _ob_a3);
                    in = _invoke(out);
                    AbstractInterfaceDef _ob_r = AbstractInterfaceDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_abstract_interface", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_abstract_interface(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_local_interface:1.0
    //
    public LocalInterfaceDef
    create_local_interface(String _ob_a0,
                           String _ob_a1,
                           String _ob_a2,
                           InterfaceDef[] _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_local_interface", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    InterfaceDefSeqHelper.write(out, _ob_a3);
                    in = _invoke(out);
                    LocalInterfaceDef _ob_r = LocalInterfaceDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_local_interface", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_local_interface(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_exception:1.0
    //
    public ExceptionDef
    create_exception(String _ob_a0,
                     String _ob_a1,
                     String _ob_a2,
                     StructMember[] _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_exception", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    StructMemberSeqHelper.write(out, _ob_a3);
                    in = _invoke(out);
                    ExceptionDef _ob_r = ExceptionDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_exception", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_exception(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_value:1.0
    //
    public ValueDef
    create_value(String _ob_a0,
                 String _ob_a1,
                 String _ob_a2,
                 boolean _ob_a3,
                 boolean _ob_a4,
                 ValueDef _ob_a5,
                 boolean _ob_a6,
                 ValueDef[] _ob_a7,
                 InterfaceDef[] _ob_a8,
                 Initializer[] _ob_a9)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_value", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    out.write_boolean(_ob_a3);
                    out.write_boolean(_ob_a4);
                    ValueDefHelper.write(out, _ob_a5);
                    out.write_boolean(_ob_a6);
                    ValueDefSeqHelper.write(out, _ob_a7);
                    InterfaceDefSeqHelper.write(out, _ob_a8);
                    InitializerSeqHelper.write(out, _ob_a9);
                    in = _invoke(out);
                    ValueDef _ob_r = ValueDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_value", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_value(_ob_a0, _ob_a1, _ob_a2, _ob_a3, _ob_a4, _ob_a5, _ob_a6, _ob_a7, _ob_a8, _ob_a9);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_value_box:1.0
    //
    public ValueBoxDef
    create_value_box(String _ob_a0,
                     String _ob_a1,
                     String _ob_a2,
                     IDLType _ob_a3)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_value_box", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    IDLTypeHelper.write(out, _ob_a3);
                    in = _invoke(out);
                    ValueBoxDef _ob_r = ValueBoxDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_value_box", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_value_box(_ob_a0, _ob_a1, _ob_a2, _ob_a3);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/Container/create_native:1.0
    //
    public NativeDef
    create_native(String _ob_a0,
                  String _ob_a1,
                  String _ob_a2)
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("create_native", true);
                    RepositoryIdHelper.write(out, _ob_a0);
                    IdentifierHelper.write(out, _ob_a1);
                    VersionSpecHelper.write(out, _ob_a2);
                    in = _invoke(out);
                    NativeDef _ob_r = NativeDefHelper.read(in);
                    return _ob_r;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("create_native", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    return _ob_self.create_native(_ob_a0, _ob_a1, _ob_a2);
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }

    //
    // IDL:omg.org/CORBA/IRObject/destroy:1.0
    //
    public void
    destroy()
    {
        while(true)
        {
            if(!this._is_local())
            {
                org.omg.CORBA.portable.OutputStream out = null;
                org.omg.CORBA.portable.InputStream in = null;
                try
                {
                    out = _request("destroy", true);
                    in = _invoke(out);
                    return;
                }
                catch(org.omg.CORBA.portable.RemarshalException _ob_ex)
                {
                    continue;
                }
                catch(org.omg.CORBA.portable.ApplicationException _ob_aex)
                {
                    final String _ob_id = _ob_aex.getId();
                    in = _ob_aex.getInputStream();

                    throw new org.omg.CORBA.UNKNOWN("Unexpected User Exception: " + _ob_id);
                }
                finally
                {
                    _releaseReply(in);
                }
            }
            else
            {
                org.omg.CORBA.portable.ServantObject _ob_so = _servant_preinvoke("destroy", _ob_opsClass);
                if(_ob_so == null)
                    continue;
                ContainerOperations _ob_self = (ContainerOperations)_ob_so.servant;
                try
                {
                    _ob_self.destroy();
                    return;
                }
                finally
                {
                    _servant_postinvoke(_ob_so);
                }
            }
        }
    }
}
