package org.experimental;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.inductiveautomation.ignition.common.util.RAFCircularBuffer;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by mattgross on 10/24/2016.
 */
public class LogExporter {

    public static void main(String[] args){
        File file = null;
        if (args.length > 0) {
            file = new File(args[0]);
        } else {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            int option = chooser.showOpenDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
            }
        }

        extractLogs(file);
    }

    public static void extractLogs(File inputFile){
        if (inputFile != null) {
            if (!inputFile.exists()) {
                System.err.printf("File \"%s\" doesn't exist.");
            } else if (!inputFile.getName().toLowerCase().endsWith(".bin.gz")) {
                System.err.printf("File \"%s\" is not a binary log file. Exiting.", inputFile.getPath());
            } else {
                System.out.printf("Loading log file \"%s\"...\n", inputFile.getPath());

                try {
                    File temp = File.createTempFile("logs",".bin");
                    temp.deleteOnExit();

                    System.out.printf("Exploding log file to \"%s\"...\n", temp.getPath());

                    GZIPInputStream in = new GZIPInputStream(new FileInputStream(inputFile));
                    FileOutputStream out = new FileOutputStream(temp);
                    IOUtils.copy(in, out);
                    in.close();
                    out.close();

                    // Loggers listed here will not make it to the output file. todo move to someplace more permanent
                    List<String> filteredLoggers = Arrays.asList(new String[] {"SubscriptionManager", "S7400Driver"});

                    RAFCircularBuffer<LoggingEvent> buffer = new RAFCircularBuffer<LoggingEvent>(temp, 1024 * 20, false);
                    RAFCircularBuffer.Filter<LoggingEvent> filter = new RAFCircularBuffer.Filter<LoggingEvent>(){

                        @Override
                        public boolean accept(LoggingEvent object) {
                            for(String filtered: filteredLoggers){
                                if(object.getLoggerName().contains(filtered)){
                                    return false;
                                }
                            }
                            return true;
                        }

                        @Override
                        public boolean finished() {
                            return false;
                        }
                    };

                    List<LoggingEvent> events = buffer.iterateForward(filter);
                    String dateFormat = "MM/dd/yyyy HH:mm:ss,SSS";
                    SimpleDateFormat df = new SimpleDateFormat(dateFormat);
                    String formatted = "%s [%s] [%s]: %s\n";

                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    chooser.setFileFilter(new FileFilter() {
                        @Override
                        public boolean accept(File file) {
                            return true;
                        }

                        @Override
                        public String getDescription() {
                            return ".log";
                        }
                    });

                    File outputFile = null;
                    int option = chooser.showSaveDialog(null);
                    if (option == JFileChooser.APPROVE_OPTION) {
                        outputFile = chooser.getSelectedFile();
                        FileWriter fw = null;

                        try{
                            fw = new FileWriter(outputFile);
                            for(LoggingEvent event: events){
                                fw.write(String.format(formatted, event.getLevel(), event.getLoggerName(),df.format(event.getTimeStamp()), event.getMessage().toString()));
                                if(event.getThrowableStrRep() != null){
                                    for(String t: event.getThrowableStrRep()){
                                        fw.write("\t" + t + "\n");
                                    }
                                }
                            }

                        }
                        finally{
                            if(fw != null){
                                fw.flush();
                                fw.close();
                            }
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
