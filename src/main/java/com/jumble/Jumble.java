/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * @author nicolas.rusconi
 **/

package com.jumble;

import static com.jumble.XmlAndConsoleReportListener.REPORT_DIR;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.reeltwo.jumble.fast.FastRunner;
import com.reeltwo.jumble.ui.EmacsFormatListener;
import com.reeltwo.jumble.ui.JumbleListener;
import com.reeltwo.jumble.ui.JumbleScorePrinterListener;
import com.reeltwo.util.CLIFlags;
import com.reeltwo.util.CLIFlags.Flag;

/**
 * A CLI interface to the <CODE>FastRunner</CODE>.
 * 
 * @author Tin Pavlinic
 * @version $Revision: 659 $
 */
public class Jumble {
  private FastRunner mFastRunner;
  
  public Jumble() {
    mFastRunner = new FastRunner();
  }
  
  /**
   * Main method.
   * 
   * @param args command line arguments. Use -h to see the expected arguments.
   */
  public static void main(String[] args) throws Exception {
    new Jumble().runMain(args);
  }

  @SuppressWarnings("rawtypes")
public void runMain(String[] args) throws Exception {
    final CLIFlags flags = new CLIFlags("Jumble");
    final Flag verboseFlag = flags.registerOptional('v', "verbose", "Provide extra output during run.");
    final Flag<String> exFlag = flags.registerOptional('x', "exclude", String.class, "METHOD", "Comma-separated list of methods to exclude.");
    final Flag retFlag = flags.registerOptional('r', "return-vals", "Mutate return values.");
    final Flag inlFlag = flags.registerOptional('k', "inline-consts", "Mutate inline constants.");
    final Flag incFlag = flags.registerOptional('i', "increments", "Mutate increments.");
    final Flag cpoolFlag = flags.registerOptional('w', "cpool", "Mutate constant pool entries.");
    final Flag switchFlag = flags.registerOptional('j', "switch", "Mutate switch cases.");
    final Flag storesFlag = flags.registerOptional('X', "stores", "Mutate assignments.");
    final Flag emacsFlag = flags.registerOptional('e', "emacs", "Use Emacs-format output (shortcut for --printer=" + EmacsFormatListener.class.getName() + ").");
    final Flag<String> jvmargFlag = flags.registerOptional('J', "jvm-arg", String.class, "STRING", "Additional command-line argument passed to the JVM used to run unit tests.");
    jvmargFlag.setMaxCount(Integer.MAX_VALUE);
    final Flag<String> jvmpropFlag = flags.registerOptional('D', "define-property", String.class, "STRING", "Additional system property to define in the JVM used to run unit tests.");
    jvmpropFlag.setMaxCount(Integer.MAX_VALUE);
    final Flag<String> printFlag = flags.registerOptional('p', "printer", String.class, "CLASS", "Name of the class responsible for producing output.");
    final Flag<Integer> firstFlag = flags.registerOptional('f', "first-mutation", Integer.class, "NUM", "Index of the first mutation to attempt (this is mainly useful for testing). Negative values are ignored.");
    final Flag<String> classpathFlag = flags.registerOptional('c', "classpath", String.class, "CLASSPATH", "The classpath to use for tests.", System.getProperty("java.class.path"));
    final Flag orderFlag = flags.registerOptional('o', "no-order", "Do not order tests by runtime.");
    final Flag saveFlag = flags.registerOptional('s', "no-save-cache", "Do not save cache.");
    final Flag loadFlag = flags.registerOptional('l', "no-load-cache", "Do not load cache.");
    final Flag useFlag = flags.registerOptional('u', "no-use-cache", "Do not use cache.");
    final Flag<String> deferFlag = flags.registerOptional('d', "defer-class", String.class, "NAME", "Defer loading of the named class/package to the parent classloader.");
    deferFlag.setMaxCount(Integer.MAX_VALUE);
    final Flag<Integer> lengthFlag = flags.registerOptional('m', "max-external-mutations", Integer.class, "MAX", "Maximum number of mutations to run in the external JVM.");
    final Flag<String> classFlag = flags.registerRequired(String.class, "CLASS", "Name of the classes to mutate.");
    classFlag.setMaxCount(1);
    classFlag.setMaxCount(Integer.MAX_VALUE);
    final Flag<String> reportDir = flags.registerOptional('a',"reportDir",String.class, "STRING", "Directory to put the report to.");

    flags.setFlags(args);
    configureReportDir(reportDir);

    mFastRunner.setInlineConstants(inlFlag.isSet());
    mFastRunner.setReturnVals(retFlag.isSet());
    mFastRunner.setIncrements(incFlag.isSet());
    mFastRunner.setCPool(cpoolFlag.isSet());
    mFastRunner.setSwitch(switchFlag.isSet());
    mFastRunner.setStores(storesFlag.isSet());
    mFastRunner.setOrdered(!orderFlag.isSet());
    mFastRunner.setLoadCache(!loadFlag.isSet());
    mFastRunner.setSaveCache(!saveFlag.isSet());
    mFastRunner.setUseCache(!useFlag.isSet());
    mFastRunner.setVerbose(verboseFlag.isSet());
    mFastRunner.setClassPath(classpathFlag.getValue());

    if (lengthFlag.isSet()) {
      int val = lengthFlag.getValue();
      if (val >= 0) {
        mFastRunner.setMaxExternalMutations(val);
      }
    }
    if (firstFlag.isSet()) {
      int val = firstFlag.getValue();
      if (val >= -1) {
        mFastRunner.setFirstMutation(val);
      }
    }

    if (exFlag.isSet()) {
      String[] tokens = exFlag.getValue().split(",");
      for (int i = 0; i < tokens.length; i++) {
        mFastRunner.addExcludeMethod(tokens[i]);
      }
    }

    if (deferFlag.isSet()) {
      for (String val : deferFlag.getValues()) {
        mFastRunner.addDeferredClass(val);
      }
    }

    if (jvmargFlag.isSet()) {
      for (String val : jvmargFlag.getValues()) {
        mFastRunner.addJvmArg(val);
      }
    }

    if (jvmpropFlag.isSet()) {
      for (String val : jvmpropFlag.getValues()) {
        mFastRunner.addSystemProperty(val);
      }
    }

    String[] classNames = classFlag.getValue().replace('/', '.').split(" ");

    JumbleListener listener = emacsFlag.isSet() ? new EmacsFormatListener(classpathFlag.getValue()) : !printFlag.isSet() ? new JumbleScorePrinterListener()
        : getListener(printFlag.getValue());

    for (String mutableClass : classNames) {
        List<String> testList = new ArrayList<String>();
        testList.add(guessTestClassName(mutableClass));
        mFastRunner.runJumble(mutableClass, testList, listener);
    }

    if (listener instanceof XmlAndConsoleReportListener) {
        ((XmlAndConsoleReportListener)listener).writeReport();
    }
  }

  private void configureReportDir(final Flag<String> reportDir) {
      String reportDirValue = reportDir.getValue();
      reportDirValue = reportDirValue.replace('\\', '/');
      if (!reportDirValue.endsWith("/")) {
          reportDirValue = reportDirValue + '/';
      }

      System.setProperty(REPORT_DIR, reportDirValue);
  }

  /**
   * Guesses the name of a test class used for testing a particular class. It
   * assumes the following conventions:
   * <p>
   * 
   * <ul>
   * 
   * <li> Unit test classes end with <code>Test</code>
   * 
   * <li> An abstract classes are named such as <code>AbstractFoo</code> and
   * have a test class named such as <code>DummyFooTest</code>
   * 
   * </ul>
   * 
   * @param className
   *          a <code>String</code> value
   * @return the name of the test class that is expected to test
   *         <code>className</code>.
   */
  public static String guessTestClassName(String className) {
    String testName = className;
    if (className.startsWith("Abstract")) {
      testName = "Dummy" + className.substring(8);
    } else {
      final int ab = className.indexOf(".Abstract");
      if (ab != -1) {
        testName = className.substring(0, ab) + ".Dummy" + className.substring(ab + 9);
      }
    }
    final int dollar = testName.indexOf('$');
    if (dollar != -1) {
      testName = testName.substring(0, dollar);
    }
    return testName + "Test";
  }

  /**
   * Returns a <code>JumbleListener</code> instance as specified by <CODE>className</CODE>.
   * 
   * @param className
   *          name of class to instantiate.
   * @return a <CODE>JumbleListener</CODE> instance.
   */
  private static JumbleListener getListener(String className) {
    try {
      final Class<?> clazz = Class.forName(className); // Class to be found in jumble.jar
      try {
        final Constructor<?> c = clazz.getConstructor(new Class[0]);
        return (JumbleListener) c.newInstance(new Object[0]);
      } catch (IllegalAccessException e) {
        System.err.println("Invalid output class. Exception: ");
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        System.err.println("Invalid output class. Exception: ");
        e.printStackTrace();
      } catch (InstantiationException e) {
        System.err.println("Invalid output class. Exception: ");
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        System.err.println("Invalid output class. Exception: ");
        e.printStackTrace();
      }
    } catch (ClassNotFoundException e) {
      ; // too bad
    }
    throw new IllegalArgumentException("Couldn't create JumbleListener: " + className);
  }

  /**
   * A function which computes the timeout for given that the original test took
   * <CODE>runtime</CODE>
   * 
   * @param runtime
   *          the original runtime
   * @return the computed timeout
   */
  public static long computeTimeout(long runtime) {
    return runtime * 10 + 2000;
  }

  public FastRunner getFastRunner() {
    return mFastRunner;
  }

}
