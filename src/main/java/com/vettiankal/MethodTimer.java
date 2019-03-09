package com.vettiankal;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MethodTimer {

    private static final String PROGRAM_NAME = "MethodTimer";
    private static final String CONFIG_NAME = "config.yml";
    private static final String CONFIG_PATH = PROGRAM_NAME + File.separator + CONFIG_NAME;

    public static void premain(String args, Instrumentation inst) {
        Yaml yaml = new Yaml();
        TransformerConfiguration config = null;
        try(InputStream in = Files.newInputStream(Paths.get(CONFIG_PATH))) {
            config = yaml.loadAs(in, TransformerConfiguration.class);
        } catch (IOException e) {
            try {
                new File(PROGRAM_NAME).mkdirs();
                InputStream in = MethodTimer.class.getResourceAsStream("/" + CONFIG_NAME);
                OutputStream out = new FileOutputStream(CONFIG_PATH);

                byte[] buffer = new byte[1024];
                int len = in.read(buffer);
                while (len != -1) {
                    out.write(buffer, 0, len);
                    len = in.read(buffer);
                }

                out.close();
                try(InputStream inp = Files.newInputStream(Paths.get(CONFIG_PATH))) {
                    config = yaml.loadAs(inp, TransformerConfiguration.class);
                }
            } catch (IOException ex) {
                System.err.println("Could not save default config.");
                ex.printStackTrace();
            }
        }

        if(config != null) {
            inst.addTransformer(new MethodTransformer(config.getClasses()), false);
            System.out.println("Added class transformer");
        }  else {
            System.out.println("Unable to add class transformer");
        }
    }

}
