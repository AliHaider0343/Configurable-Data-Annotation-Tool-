/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.annotation_tool_for_custom_dataset;

/**
 *
 * @author Ali Haider
 */
import java.io.FileInputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ConfigurationLoader {
    
    // Get the path to the current working directory
    static String currentDir = System.getProperty("user.dir");
    
    public static JSONObject loadConfigurations()
    {
        JSONObject config =null;
        try {
            // Load the configuration file2
            JSONTokener tokener = new JSONTokener(new FileInputStream(  currentDir + "/config.json"));
            config = new JSONObject(tokener);
 
         } catch (Exception e) {
            e.printStackTrace();
        } 
        return config;
    }
    
   public static JSONObject loadState()
    {
        JSONObject config =null;
        try {
            // Load the configuration file
            JSONTokener tokener = new JSONTokener(new FileInputStream(  currentDir + "/state.json"));
            config = new JSONObject(tokener);
 
         } catch (Exception e) {
            e.printStackTrace();
        } 
        return config;
    }
   public static void main(String[] args)
   {
       ConfigurationLoader.loadConfigurations();
       
   }
}

