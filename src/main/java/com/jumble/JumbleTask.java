package com.jumble;

import java.io.File; 
import java.util.Iterator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.resources.FileResource;


public class JumbleTask extends Task {

    private boolean fork = true;
    private String exclude = "";
    private String jumbleClasspath = "jumble.jar";
    private String reportDir = "./";
    private Iterator<?> iterator;
    private String appClasspath;
    private File classesDir;
    private String[] options = new String[0];

    public void execute() throws BuildException {
        Java java = new Java(this);

        Path path = new Path(this.getProject(), generateJumbleClasspath());
        java.setClasspath(path);
        java.setDir(new File("."));
        java.setFork(fork);
        java.setClassname(Jumble.class.getCanonicalName());

        java.createArg().setValue("--reportDir=" + reportDir);
        java.createArg().setValue("--classpath=" + generateAppClasspath());
        java.createArg().setValue("--printer=" + XmlAndConsoleReportListener.class.getCanonicalName());

        for (String option : options) {
            java.createArg().setValue(option);
        }

        if (!exclude.isEmpty()) {
            java.createArg().setValue("--exclude=" + exclude);
        }

        java.createArg().setValue(lookupMutableClasses());
        java.execute();
    }

    private String generateJumbleClasspath() {
        return generateClasspath(jumbleClasspath);
    }

    private String generateAppClasspath() {
        if (appClasspath == null) {
            throw new BuildException("You must specify the appClasspathRef element.");
        }
        return generateClasspath(appClasspath)+ ";" + classesDir;
    }

    private String generateClasspath(String classpath) {
        Object reference = getProject().getReference(classpath);
        String generatedClasspath = classpath;
        if (reference != null) {
            generatedClasspath = reference.toString();
        }
        return generatedClasspath;
    }

    private String lookupMutableClasses() {
        StringBuffer classes = new StringBuffer();
        while(iterator.hasNext()) {
            FileResource file = (FileResource)iterator.next();
            classes.append(toCannonicalName(file) + " ");
        }
        return classes.toString();
    }

    private String toCannonicalName(FileResource file) {
        return file.getName().replace("\\", ".").replace(".class", "");
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
    }

    public void setJumbleClasspath(String jumbleClasspath) {
        this.jumbleClasspath = jumbleClasspath;
    }

    public void addConfiguredFileset(FileSet fileSet) {
        classesDir = fileSet.getDir();
        iterator = fileSet.iterator();
    }

    public void setReportDir(String reportDir) {
        this.reportDir = reportDir;
    }

    public void setAppClasspath(String appClasspath) {
        this.appClasspath = appClasspath;
    }

    public void setOptions(String options) {
        this.options = options.split(" ");
    }

}
