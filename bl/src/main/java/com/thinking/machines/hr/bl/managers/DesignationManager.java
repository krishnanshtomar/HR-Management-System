package com.thinking.machines.hr.bl.managers;
import com.thinking.machines.hr.bl.interfaces.pojo.*;
import com.thinking.machines.hr.bl.interfaces.managers.*;
import com.thinking.machines.hr.bl.exceptions.*;
import com.thinking.machines.hr.bl.pojo.*;
import com.thinking.machines.hr.dl.interfaces.dto.*;
import com.thinking.machines.hr.dl.interfaces.dao.*;
import com.thinking.machines.hr.dl.dto.*;
import com.thinking.machines.hr.dl.dao.*;
import com.thinking.machines.hr.dl.exception.*;
import java.util.*;

public class DesignationManager implements DesignationManagerInterface
{
private Map<Integer,DesignationInterface> codeWiseDesignationMap;
private Map<String,DesignationInterface> titleWiseDesignationMap;
private Set<DesignationInterface> designationSet;
private static DesignationManager designationManager=null;

private DesignationManager()    throws BLException
{
populateDataStructures();
}

private void populateDataStructures()    throws BLException
{
this.codeWiseDesignationMap=new HashMap<>();
this.titleWiseDesignationMap=new HashMap<>();
this.designationSet=new TreeSet<>();
try
{
Set<DesignationDTOInterface> dlDesignations;
DesignationDAOInterface designationDAO;
designationDAO=new DesignationDAO();
dlDesignations=designationDAO.getAll();
DesignationInterface designation;
for(DesignationDTOInterface dlDesignation:dlDesignations)
{
designation=new Designation();
designation.setCode(dlDesignation.getCode());
designation.setTitle(dlDesignation.getTitle());
this.codeWiseDesignationMap.put(new Integer (designation.getCode()),designation);
this.titleWiseDesignationMap.put(designation.getTitle().toUpperCase(),designation);
this.designationSet.add(designation);
}
}catch(DAOException daoException)
{
BLException blException=new BLException();
blException.setGenericException(daoException.getMessage());
throw blException;
}
}

public static DesignationManagerInterface getDesignationManager()    throws BLException
{
if(designationManager==null)    designationManager=new DesignationManager();
return designationManager;
}

public void addDesignation(DesignationInterface designation)    throws BLException
{
BLException blException;
blException=new BLException();
if(designation==null)
{
blException.setGenericException("Designation required");
throw blException;
}
int code=designation.getCode();
String title=designation.getTitle();
if(code!=0)
{
blException.addException("code","Code Should be zero");
}
if(title==null)
{
blException.addException("title","Title required");
title="";
}
else
{
title=title.trim();
if(title.length()==0)
{
blException.addException("title","Title required");
}
}
if(title.length()>0)
{
if(this.titleWiseDesignationMap.containsKey(title.toUpperCase()))
{
blException.addException("title","Designation : "+title+" exists.");
}
}
if(blException.hasExceptions())
{
throw blException;
}
try
{
DesignationDTOInterface designationDTO;
designationDTO=new DesignationDTO();
designationDTO.setTitle(title);
DesignationDAOInterface designationDAO;
designationDAO=new DesignationDAO();
designationDAO.add(designationDTO);
code=designationDTO.getCode();
designation.setCode(code);
DesignationInterface dsDesignation;
dsDesignation=new Designation();
dsDesignation.setCode(code);
dsDesignation.setTitle(title);
codeWiseDesignationMap.put(new Integer(code),dsDesignation);
titleWiseDesignationMap.put(title.toUpperCase(),dsDesignation);
designationSet.add(dsDesignation);
}catch(DAOException daoException)
{
blException.setGenericException(daoException.getMessage());
}
}

public void updateDesignation(DesignationInterface designation)    throws BLException
{
BLException blException;
blException=new BLException();
if(designation==null)
{
blException.setGenericException("Designation required");
throw blException;
}
int code=designation.getCode();
String title=designation.getTitle();
if(code<=0)
{
blException.addException("code","Invalid Code : "+code);
}
if(code>0)
{
if(this.codeWiseDesignationMap.containsKey(new Integer(code))==false)
{
blException.addException("code","Invalid Code : "+code);
}
}
if(title==null)
{
blException.addException("title","Title required");
title="";
}
else
{
title=title.trim();
if(title.length()==0)
{
blException.addException("title","Title required");
}
}
if(title.length()>0)
{
DesignationInterface d;
d=titleWiseDesignationMap.get(title.toUpperCase());
if(d!=null && d.getCode()!=code)
{
blException.addException("title","Designation : "+title+" exists.");
}
}
if(blException.hasExceptions())
{
throw blException;
}
try
{
DesignationInterface dsDesignation=codeWiseDesignationMap.get(code);
DesignationDTOInterface dlDesignation;
dlDesignation=new DesignationDTO();
dlDesignation.setCode(code);
dlDesignation.setTitle(title);
DesignationDAOInterface designationDAO;
designationDAO=new DesignationDAO();
designationDAO.update(dlDesignation);
//remove the old one from all DS
codeWiseDesignationMap.remove(code);
titleWiseDesignationMap.remove(dsDesignation.getTitle().toUpperCase());
designationSet.remove(dsDesignation);
//update the DS object
dsDesignation.setTitle(title);
//update the DS
codeWiseDesignationMap.put(code,dsDesignation);
titleWiseDesignationMap.put(title.toUpperCase(),dsDesignation);
designationSet.add(dsDesignation);
}catch(DAOException daoException)
{
blException.setGenericException(daoException.getMessage());
}
}

public void removeDesignation(int code)    throws BLException
{
BLException blException;
blException=new BLException();
if(code<=0)
{
blException.addException("code","Invalid Code : "+code);
throw blException;
}
if(code>0)
{
if(this.codeWiseDesignationMap.containsKey(new Integer(code))==false)
{
blException.addException("code","Invalid Code : "+code);
throw blException;
}
}
try
{
DesignationInterface dsDesignation=codeWiseDesignationMap.get(code);
DesignationDAOInterface designationDAO;
designationDAO=new DesignationDAO();
designationDAO.delete(code);
//remove the old one from all DS
codeWiseDesignationMap.remove(code);
titleWiseDesignationMap.remove(dsDesignation.getTitle().toUpperCase());
designationSet.remove(dsDesignation);
}catch(DAOException daoException)
{
blException.setGenericException(daoException.getMessage());
throw blException;
}
}

public DesignationInterface getDesignationByCode(int code)    throws BLException
{
DesignationInterface designation;
designation=codeWiseDesignationMap.get(code);
if(designation==null)
{
BLException blException;
blException=new BLException();
blException.addException("code","Invalid Code : "+code);
throw blException;
}
DesignationInterface d=new Designation();
d.setCode(designation.getCode());
d.setTitle(designation.getTitle());
return designation;
}

public DesignationInterface getDesignationByTitle(String title)    throws BLException
{
DesignationInterface designation;
designation=titleWiseDesignationMap.get(title.toUpperCase());
if(designation==null)
{
BLException blException;
blException=new BLException();
blException.addException("code","Invalid Designation Title : "+title);
throw blException;
}
DesignationInterface d=new Designation();
d.setCode(designation.getCode());
d.setTitle(designation.getTitle());
return designation;
}

public int getDesignationCount()
{
return designationSet.size();
}

public boolean designationCodeExists(int code)
{
return codeWiseDesignationMap.containsKey(code);
}

public boolean designationTitleExists(String title)
{
return titleWiseDesignationMap.containsKey(title.toUpperCase());
}

public Set<DesignationInterface> getDesignations()
{
Set<DesignationInterface> designations;
designations=new TreeSet<>();
designationSet.forEach((designation)->{
DesignationInterface d=new Designation();
d.setCode(designation.getCode());
d.setTitle(designation.getTitle());
designations.add(d);
});
return designations;
}

}