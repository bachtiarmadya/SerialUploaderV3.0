/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.bmp.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 *
 * @author permadi
 */
public class FlowmeterConfiguration {
        public String setFlowmeter() throws IOException {

        String data = "";
        String fileName = "properties/flowmeter.gen.txt";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }
}
