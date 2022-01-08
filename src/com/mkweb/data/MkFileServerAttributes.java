package com.mkweb.data;

import java.util.ArrayList;

public class MkFileServerAttributes {
    String tableName;
    String controllerName;
    String serviceName;
    String fileDirectory;
    String fileFormat;
    String fileHash;
    String fileAlive;
    String sqlControlName;
    String sequence;

    public String get(String key){
        switch(key){
            case "seq": { return getSequenceAttr(); }
            case "table":{  return getTableNameAttr();  }
            case "control":{    return getControllerNameAttr(); }
            case "service":{    return getServiceNameAttr();    }
            case "filedir":{    return getFileDirectoryAttr();  }
            case "format":{ return getFileFormatAttr();   }
            case "filehash":{   return getFileHashAttr();   }
            case "filealive":{  return getFileAliveAttr();  }
            default:{   return null;    }
        }
    }
    public String getSequenceAttr(){ return this.sequence;  }
    public String getTableNameAttr(){   return this.tableName;  }
    public String getControllerNameAttr(){  return controllerName;  }
    public String getServiceNameAttr(){ return serviceName; }
    public String getFileDirectoryAttr(){   return fileDirectory;   }
    public String getFileFormatAttr(){ return fileFormat; }
    public String getFileHashAttr(){ return fileHash; }
    public String getFileAliveAttr(){ return fileAlive; }
    public String getSqlControlName(){ return this.sqlControlName;  }

    public MkFileServerAttributes setSequenceAttr(String seq){ this.sequence = seq; return this; }
    public MkFileServerAttributes setTableName(String tableName){   this.tableName = tableName; return this;}
    public MkFileServerAttributes setControllerAttr(String controllerName){this.controllerName = controllerName;   return this;}
    public MkFileServerAttributes setServiceAttr(String serviceName){this.serviceName = serviceName;   return this;}
    public MkFileServerAttributes setFileDirectory(String fileDirectory){ this.fileDirectory = fileDirectory; return this;  }
    public MkFileServerAttributes setFormatAttr(String fileOriginal){this.fileFormat = fileOriginal; return this;    }
    public MkFileServerAttributes setFileHashAttr(String fileHash){this.fileHash = fileHash;    return this;}
    public MkFileServerAttributes setFileAliveAttr(String fileAlive){this.fileAlive = fileAlive;    return this;    }
    public MkFileServerAttributes setSqlControlName(String sqlName){ this.sqlControlName = sqlName; return this;    }
}
