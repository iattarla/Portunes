/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portunesv2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author bilal
 */
public class Door {
    
     String tarla_id = "";
     String ent_time = "";
     String ext_time = "";
     SimpleDateFormat ft;
     
        
     Connection con = null;
     Statement st = null;
     ResultSet rs = null;

     Config config = new Config();
    
     private final String db_domain = config.db_domain;        //"localhost:3307";
     private final String db_url = "jdbc:mysql://" + config.db_domain + "/"+config.db_name;                 //"jdbc:mysql://" + db_domain + "/saglamdis"; // 192.241.172.225:3306
     private final String db_user = config.db_user;           //"saglamdisuser";
     private final String db_password = "super_251";
    
     private final String db_table = "doors";
    
    
     public Door(){
    
         //ft  = new SimpleDateFormat ("yyyy.MM.dd hh:mm:ss");

         //System.out.println("Datetime: " + ft.format(ent_time));
     
     }
    
     
    public void Create(){
        
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
                Date date = new Date();
          
              try {
                    con = (Connection) DriverManager.getConnection(db_url, db_user, db_password);
                    st = (Statement) con.createStatement();
                    
                    String insertTableSQL = "INSERT INTO " + db_table + " (tarla_id,ent_time,ext_time)" + 
                                          "VALUES"
                        + "(?,?,?)";
                    
                    PreparedStatement preparedStatement = con.prepareStatement(insertTableSQL);
                    preparedStatement.setString(1,tarla_id);
                    preparedStatement.setString(2, ent_time);
                    preparedStatement.setString(3, ext_time);
                   
                    
                    // execute insert SQL stetement
                    
                    preparedStatement .executeUpdate();
                    
                  
                } catch (SQLException ex) {
                    System.out.println(ex);
                    Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi

                } finally {
                    
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                           st.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        System.out.println(ex);
                        Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi
                    }
                }   
                
            
    }
    
    public void Update(){
        
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                
                Date date = new Date();
          
              try {
                    con = (Connection) DriverManager.getConnection(db_url, db_user, db_password);
                    st = (Statement) con.createStatement();
                    
                    System.out.println(ext_time);
                    
                    st.executeUpdate("UPDATE `redmine`.`doors` SET " + //`saglamdis`.`appointments`
                                                    "ext_time = '" + ext_time + "'" +
                                      " where tarla_id = '" + tarla_id + "' ;");

                    
                  
                  
                } catch (SQLException ex) {
                    System.out.println(ex);
                    Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi

                } finally {
                    
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                           st.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        System.out.println(ex);
                        Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi
                    }
                }   
                
            
    }
    
    public boolean Select(String search_tarla_id,String search_ent_time){
       
        System.out.println("search ent" + search_ent_time);
        
        try {
            con = (Connection) DriverManager.getConnection(db_url, db_user, db_password);
            st = (Statement) con.createStatement();    
            rs = st.executeQuery("SELECT * FROM " + db_table + " WHERE date(ent_time) = '" + search_ent_time + "' and tarla_id = '"+search_tarla_id+"' and ext_time = '0000-00-00 00:00:00' ");


                    if (rs.next()) {
                        
                            tarla_id = rs.getString("tarla_id");
                            ent_time = rs.getString("ent_time");
                            System.out.println(tarla_id + ent_time );
                            return true;
                           
                    }else{// cant find any record!
                        return false;
                    }
                  
                } catch (SQLException ex) {
                    System.out.println(ex);
                    Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi
                } finally {
                    
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (st != null) {
                           st.close();
                        }
                        if (con != null) {
                            con.close();
                        }

                    } catch (SQLException ex) {
                        System.out.println(ex);    
                        Exception exception = new Exception(ex,this.getClass().toString()); // mail ile hata bildirimi
                    }
                }   
        
          return false;     
}
    
}
