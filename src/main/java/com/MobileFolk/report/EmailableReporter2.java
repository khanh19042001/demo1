package com.MobileFolk.report;

import com.MobileFolk.EmailConfig.Email;
import com.MobileFolk.EmailConfig.EmailUtil;
import com.MobileFolk.constants.FrameworkConst;
import org.testng.*;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.testng.ITestResult.*;

public class

EmailableReporter2 implements IReporter {

    private static final Logger LOG = Logger.getLogger(EmailableReporter2.class);
    protected PrintWriter writerReport;
    protected PrintWriter writerSuite, writerResult;
    protected final List<SuiteResult> suiteResults = Lists.newArrayList();
    private final StringBuilder buffer = new StringBuilder();
    private String reportFileName = "emailable_report2.html";
    private String suiteFileName = "test-failed.xml";

    private String resultFileName = "test-result.txt";

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        deleteResultFile("ExtentReports", resultFileName);
        try {
            writerReport = createWriter("ExtentReports", reportFileName);
            writerSuite = createWriter("ExtentReports", suiteFileName);
            writerResult = createWriter("ExtentReports", resultFileName);
        } catch (IOException e) {
            LOG.error("Unable to create output file", e);
        }
        for (ISuite suite : suites) {
            suiteResults.add(new SuiteResult(suite));
        }

        writeDocumentStart();
        writeHead();
        writeBody();
        writeDocumentEnd();

        writerReport.close();
        writerSuite.close();
        writerResult.close();


        String host = "smtp.gmail.com";
        String port = "465";
        String username = "minsoah1904@gmail.com";
        String password = "ytavfajofrmoiplq";//ytavfajofrmoiplq -qrifvnfdymfuifmp
        String from = "minsoah1904@gmail.com";
//        String[] recipients = {"ngan@mobilefolk.com", "thao@mobilefolk.com", "Khanhthi455@gmail.com", "viet@mobilefolk.com"};
        String[] recipients = {"Khanhthi455@gmail.com"};

        Email email;
        email = new Email(host, port, username, password, from, recipients);
        EmailUtil emailSender = new EmailUtil();

        emailSender.sendEmail(email);
    }


    protected PrintWriter createWriter(String outDir, String fileName) throws IOException {

        File file = new File(outDir);
        if (!file.exists()) file.mkdirs();
        OutputStream os = Files.newOutputStream(Paths.get(outDir + File.separator + fileName));
        return new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
    }

    public void deleteResultFile(String outDir, String fileName) {
        try {
            String filePath = outDir + File.separator + fileName;
            Path path = Path.of(filePath);


            Files.deleteIfExists(path);
            System.out.println("Đã xóa tệp tin " + filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void writeReportTitle() {
        String s = "<p>Gửi các thành viên dự án ELEGANT, <br><br>Automation Team gửi báo cáo kết quả thực thi dự án " + FrameworkConst.PROJECT_NAME + ", phiên bản <b> version_build</b><br></p>" +
                "<p></p> <p>Build result: <b>build_result</b><br></p>" +
                "<p> Report Links: <br></p> <p>&emsp;<a href=\"ExtentReportLink\">- Extent Report Link </a>" +
                "<p><i>(Đây là mail phát hành tự động sau khi thực thi. Vui lòng liên hệ Automation Team nếu bạn cần thêm thông tin!)</i>.</p>";
        writerReport.println(s);
        writerReport.println("<p>=================================================</p><br><h4>Report Detail:</h4>");
    }

    protected void writeDocumentStart() {
        writerReport.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
        writerReport.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
    }

    protected void writeHead() {
        writerReport.println("<head>");
        writerReport.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
        writerReport.println(String.format("<title>%s</title>", FrameworkConst.REPORT_TITLE));
        writeStylesheet();
        writerReport.println("</head>");
    }

    protected void writeStylesheet() {
        writerReport.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">");
        writerReport.print("<style type=\"text/css\">");
        writerReport.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writerReport.print("#summary {margin-top:30px}");
        writerReport.print("h1 {font-size:30px}");
        writerReport.print("body {width:100%;}");
        writerReport.print(".alignLeft {text-align: left;}");
        writerReport.print("th,td {padding: 10px; border-style: none ridge ridge none;}");
        writerReport.print("th {vertical-align:top;background-color:#33CC33}");
        writerReport.print("td {vertical-align:top}");
        writerReport.print("table a {font-weight:bold;color:#0D1EB6;}");
        writerReport.print(".easy-overview {margin-left: auto; margin-right: auto;} ");
        writerReport.print(".easy-test-overview tr:first-child {background-color:#D3D3D3}");
        writerReport.print(".stripe td {background-color: #E6EBF9}");
        writerReport.print(".num {text-align:right}");
        writerReport.print(".numbold {text-align:right;font-weight: bold;}");
        writerReport.print(".passed {color: #33CC33;font-weight: bold}");
        writerReport.print(".skipped {color: #DDD;font-weight: bold}");
        writerReport.print(".failed {color: #F33;font-weight: bold}");
        writerReport.print(".stacktrace {font-family:monospace}");
        writerReport.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writerReport.print(".invisible {display:none}");
        writerReport.println("</style>");
    }

    protected void writeBody() {
        writerReport.println("<body>");
        writeReportTitle();
        writeSuiteSummary();
        writeScenarioSummary();
        writerReport.println("</body>");
    }

    protected void writeDocumentEnd() {
        writerReport.println("</html>");
    }

    protected void writeSuiteSummary() {
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

        int totalTestsCount = 0;
        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;
        long totalDuration = 0;

        writerReport.println("<div class=\"easy-test-overview\">");
        writerReport.println("<table class=\"table-bordered\">");
        writerReport.print("<tr>");
        writerReport.print("<th>Test/Class</th>");
        writerReport.print("<th>No. Test</th>");
        writerReport.print("<th>Passed</th>");
        writerReport.print("<th>Failed</th>");
        writerReport.print("<th>Skipped</th>");
        writerReport.print("<th>Start Time</th>");
        writerReport.print("<th>End Time</th>");
        writerReport.print("<th>Total Time</th>");
        writerReport.println("</tr>");

        int testIndex = 0;
        for (SuiteResult suiteResult : suiteResults) {
            writerReport.print("<tr><th colspan=\"8\" class=\"alignLeft\">");
            writerReport.print(Utils.escapeHtml("Test suite: " + suiteResult.getSuiteName()));
            writerReport.println("</th></tr>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                int testsCount = testResult.getTestCount();
                int passedTests = testResult.getPassedTestCount();
                int skippedTests = testResult.getSkippedTestCount();
                int failedTests = testResult.getFailedTestCount();

                Date startTime = testResult.getTestStartTime();
                Date endTime = testResult.getTestEndTime();
                long duration = testResult.getDuration();

                writerReport.print("<tr");
                if ((testIndex % 2) == 1) {
                    writerReport.print(" class=\"stripe\"");
                }
                writerReport.print(">");

                buffer.setLength(0);
                writeTableData(buffer.append("<strong>")
                        .append(Utils.escapeHtml("Test: " + testResult.getTestName()))
                        .append("</strong>").toString());
                writeTableData(integerFormat.format(testsCount), "numbold");
                writeTableData(integerFormat.format(passedTests), "numbold");
                writeTableData(integerFormat.format(failedTests), "numbold");
                writeTableData(integerFormat.format(skippedTests), "numbold");
                writeTableData(dateFormat.format(startTime), "numbold");
                writeTableData(dateFormat.format(endTime), "numbold");
                writeTableData(convertTimeToString(duration), "numbold");
                writerReport.println("</tr>");

                totalTestsCount += testsCount;
                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                totalDuration += duration;
                testIndex++;


                List<ClassResult> classResults = testResult.getAllClassTestResults();
                for (ClassResult classResult : classResults) {
                    buffer.setLength(0);
                    String className = classResult.getClassName();
                    writeTableData(buffer.append(Utils.escapeHtml(className.substring(className.lastIndexOf(".") + 1))).toString());
                    writeTableData(integerFormat.format(classResult.getMethodResults().size()), "num");
                    writeTableData(integerFormat.format(classResult.nPassTC), "num");
                    writeTableData(integerFormat.format(classResult.nFailTC), "num");
                    writeTableData(integerFormat.format(classResult.nSkipTC), "num");
                    writeTableData(dateFormat.format(classResult.start), "num");
                    writeTableData(dateFormat.format(classResult.end), "num");
                    writeTableData(convertTimeToString(classResult.end - classResult.start), "num");
                    writerReport.println("</tr>");
                }

            }
        }

        // Print totals if there was more than one test
        if (testIndex > 1) {
            writerReport.print("<tr>");
            writerReport.print("<th>Total</th>");
            writeTableHeader(integerFormat.format(totalTestsCount), "num");
            writeTableHeader(integerFormat.format(totalPassedTests), "num");
            writeTableHeader(integerFormat.format(totalFailedTests), "num");
            writeTableHeader(integerFormat.format(totalSkippedTests), "num");
            writerReport.print("<th colspan=\"2\"></th>");
            writeTableHeader(convertTimeToString(totalDuration), "num");
            writerReport.println("</tr>");
        }

        writerReport.println("</table>");
        writerReport.println("</div>");
    }

    /**
     * Writes a summary of all the test scenarios.
     */
    protected void writeScenarioSummary() {
        writerReport.println("<br><h4>List FAIL and SKIP testcases:</h4>");
        writerReport.print("<div class=\"easy-test-summary\">");
        writerReport.print("<table class=\"table-bordered\" id='summary'>");
        writerReport.print("<thead>");
        writerReport.print("<tr>");
        writerReport.print("<th>Class</th>");
        writerReport.print("<th>Method</th>");
        writerReport.print("<th>Description</th>");
        writerReport.print("<th>Start Time</th>");
        writerReport.print("<th>End Time</th>");
        writerReport.print("<th>Duration</th>");
        writerReport.print("<th>Test Result</th>");
        writerReport.print("</tr>");
        writerReport.print("</thead>");

        int testIndex = 0;
        int scenarioIndex = 0;
        writerSuite.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE suite SYSTEM \"https://testng.org/testng-1.0.dtd\">");
        writerResult.println("Testcase\tDescription\tClass\tResult");
        for (SuiteResult suiteResult : suiteResults) {
            /*
            writer.print("<tbody><tr><th colspan=\"6\" class=\"alignLeft\">");
            writer.print(Utils.escapeHtml("Test Suite: " + suiteResult.getSuiteName()));
            writer.print("</th></tr></tbody>");
            */
            writerSuite.println(String.format("<suite name=\"%s - Retry\" parallel=\"%s\" thread-count=\"%s\">", suiteResult.getSuiteName(), suiteResult.parallel, suiteResult.noThread));
            if (suiteResult.listeners.size() > 0) {
                writerSuite.println("\t<listeners>");
                suiteResult.listeners.stream().forEach(item -> {
                    writerSuite.println(String.format("\t\t<listener class-name=\"%s\"/>", item));
                });
                writerSuite.println("\t</listeners>");
            }
            if (suiteResult.parameters.size() > 0) {
                suiteResult.parameters.entrySet().stream().forEach(item -> {
                    writerSuite.println(String.format("\t<parameter name=\"%s\" value=\"%s\"/>", item.getKey(), item.getValue()));
                });
            }
            writerResult.println(suiteResult.getSuiteName());
            for (TestResult testResult : suiteResult.getTestResults()) {
                writerReport.printf("<tbody id=\"t%d\" class=\"alignLeft\">", testIndex);

                String testName = Utils.escapeHtml(testResult.getTestName());
                int startIndex = scenarioIndex;
                writerSuite.println(String.format("\t<test name=\"%s - Retry\">", testResult.getTestName()));
                writerResult.println(testResult.getTestName());
                if (testResult.parameters.size() > 0) {
                    testResult.parameters.entrySet().stream().forEach(item -> {
                        writerSuite.println(String.format("\t\t<parameter name=\"%s\" value=\"%s\"/>", item.getKey(), item.getValue()));
                    });
                }
                scenarioIndex += writeScenarioSummary(testName, testResult.getAllClassTestResults(), scenarioIndex);

                if (scenarioIndex == startIndex) {
                    writerReport.print("<tr><th colspan=\"4\" class=\"invisible\"/></tr>");
                }

                for (ITestNGMethod method : testResult.methods) {
                    if (method.getEnabled()) {
                        writerSuite.println(String.format("<!--<class name=\"%s\">\n<method>\n<include name=\"%s\"/>\n</method>\n</class>-->", method.getTestClass().getName(), method.getMethodName()));
                        writerResult.println(String.format("%s\t%s\t%s\t%s", method.getMethodName(), method.getDescription(), method.getTestClass().getName(), "SKIPPED"));
                    }
                }

                writerReport.println("</tbody>");
                writerSuite.println("\t</test>");

                testIndex++;
            }
            writerSuite.println("</suite>");
        }

        writerReport.println("</table>");
        writerReport.println("</div>");
    }

    /**
     * Writes the scenario summary for the results of a given state for a single
     * test.
     */
    private int writeScenarioSummary(String description, List<ClassResult> classResults, int startingScenarioIndex) {
        int scenarioCount = 0;
        if (!classResults.isEmpty()) {
            /*
            writer.print("<tr><th colspan=\"6\">Test class: ");
            writer.print(description);
            writer.print("</th></tr>");
             */
            writerSuite.println("\t\t<classes>");
            int scenarioIndex = startingScenarioIndex;
            for (ClassResult classResult : classResults) {
                buffer.setLength(0);
                int scenariosPerClass = 0;
                boolean isFirst = true;
                String className = classResult.getClassName();
                className = className.substring(className.lastIndexOf(".") + 1);
                StringBuilder testBuffer = new StringBuilder();
                Set<String> dependMethods = new HashSet<>();
                for (MethodResult methodResult : classResult.getMethodResults()) {
                    StringBuilder sb = new StringBuilder();
                    List<ITestResult> results = methodResult.getResults();
                    int resultsCount = results.size();
                    String methodName = Utils.escapeHtml(methodResult.methodName);
                    for (ITestResult result : results) {
                        String desc = result.getAttributeNames().contains("description") ? (String) result.getAttribute("description") : result.getMethod().getDescription();
                        String invo = result.getAttributeNames().contains("invocation") ? result.getAttribute("invocation").toString() : "0";

                        String testResult = "";
                        if (result.getStatus() == SUCCESS) {
                            testResult = "passed";
                            resultsCount--;
                        } else if (result.getStatus() == FAILURE)
                            testResult = "failed";
                        else if (result.getStatus() == SKIP)
                            testResult = "skipped";

                        writerResult.println(String.format("%s\t[%s] %s\t%s\t%s", methodName, invo, desc, className, testResult.toUpperCase()));

                        if (testResult.equals("passed"))
                            continue;
                        DateFormat formatter = new SimpleDateFormat("hh:mm:ss a");
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTimeInMillis(result.getStartMillis());

                        Calendar endTime = Calendar.getInstance();
                        endTime.setTimeInMillis(result.getEndMillis());

                        if (!isFirst) {
                            buffer.append("<tr>");
                            isFirst = false;
                        }

                        sb.append(invo).append(" ");
                        buffer.append("<td>").append(methodName)
                                .append("</td><td rowspan=\"\">").append(String.format("[%s] %s", invo, desc))
                                .append("</td><td rowspan=\"\">").append(formatter.format(startTime.getTime()))
                                .append("</td><td rowspan=\"\">").append(formatter.format(endTime.getTime()))
                                .append("</td><td rowspan=\"\">").append(convertTimeToString(result.getEndMillis() - result.getStartMillis()))
                                .append("</td><td rowspan=\"\" class=\"")
                                .append(testResult).append("\">")
                                .append(testResult.toUpperCase())
                                .append("</td></tr>");
                        scenarioIndex++;

                    }
                    scenariosPerClass += resultsCount;
                    if (sb.length() > 0) {
                        testBuffer.append(String.format("\t\t\t\t\t<include name=\"%s\" invocation-numbers=\"%s\"/>\n", methodResult.methodName, sb.deleteCharAt(sb.length() - 1)));
                        for (String method : methodResult.getResults().get(0).getMethod().getMethodsDependedUpon()) {
                            dependMethods.add(method.substring(method.lastIndexOf(".") + 1));
                        }
                    }
                }

                if (classResult.nFailTC == 0 && classResult.nSkipTC == 0)
                    continue;
                writerSuite.println(String.format("\t\t\t<class name=\"%s\">\n\t\t\t\t<methods>", classResult.getClassName()));

                String strTestBuffer = testBuffer.toString();
                for (String str : dependMethods) {
                    if (!strTestBuffer.contains(str))
                        writerSuite.println(String.format("\t\t\t\t\t<include name=\"%s\"/>", str));
                }
                writerSuite.println(strTestBuffer);

                // Write the test results for the class
                writerReport.print("<tr>");
                writerReport.print("<td rowspan=\"");
                writerReport.print(scenariosPerClass);
                writerReport.print("\"><strong>");
                writerReport.print(Utils.escapeHtml(className));
                writerReport.print("</strong></td>");
                writerReport.print(buffer);
                writerSuite.println("\t\t\t\t</methods>\n\t\t\t</class>");
            }
            writerSuite.println("\t\t</classes>");
            scenarioCount = scenarioIndex - startingScenarioIndex;
        }
        return scenarioCount;
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     *
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     *
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS
     * class names.
     *
     * @param tag        the tag name
     * @param html       the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no
     *                   classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writerReport.print("<");
        writerReport.print(tag);
        if (cssClasses != null) {
            writerReport.print(" class=\"");
            writerReport.print(cssClasses);
            writerReport.print("\"");
        }
        writerReport.print(">");
        writerReport.print(html);
        writerReport.print("</");
        writerReport.print(tag);
        writerReport.print(">");
    }

    /**
     * Groups {@link TestResult}s by suite.
     */
    protected static class SuiteResult {
        private final String suiteName;
        private String parallel;
        private int noThread;
        private Map<String, String> parameters;
        private List<String> listeners;
        private final List<TestResult> testResults = Lists.newArrayList();

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            parallel = suite.getParallel();
            noThread = suite.getXmlSuite().getThreadCount();
            listeners = suite.getXmlSuite().getListeners();
            parameters = suite.getXmlSuite().getParameters();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                testResults.add(new TestResult(suiteResult.getTestContext()));
            }
        }

        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @return the test results (possibly empty)
         */
        public List<TestResult> getTestResults() {
            return testResults;
        }
    }

    /**
     * Groups {@link ClassResult}s by test, type (configuration or test), and
     * status.
     */
    protected static class TestResult {
        /**
         * Orders test results by class name and then by method name (in
         * lexicographic order).
         */
        protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
            @Override
            public int compare(ITestResult o1, ITestResult o2) {
                int result = o1.getTestClass().getName()
                        .compareTo(o2.getTestClass().getName());
                if (result == 0) {
                    result = o1.getMethod().getMethodName()
                            .compareTo(o2.getMethod().getMethodName());
                    if (result == 0) {
                        result = (int) (o1.getStartMillis() - o2.getStartMillis());
                    }
                }
                return result;
            }
        };

        private final String testName;
        private final Date testStartTime;
        private final Date testEndTime;
        private List<ClassResult> allClassTestResults;
        private List<ClassResult> allConfigurationResults;
        private final int failedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final int testCount;
        private final long duration;

        private Map<String, String> parameters;

        private List<ITestNGMethod> methods;

        public TestResult(ITestContext context) {
            testName = context.getName();

            Set<ITestResult> failedConfigurations = context.getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
            Set<ITestResult> skippedConfigurations = context.getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults();
            Set<ITestResult> passedTests = context.getPassedTests().getAllResults();

            Set<ITestResult> allTestResult = new HashSet<>();
            Set<ITestResult> allConfigResult = new HashSet<>();
            allConfigResult.addAll(failedConfigurations);
            allConfigResult.addAll(skippedConfigurations);
            allTestResult.addAll(failedTests);
            allTestResult.addAll(skippedTests);
            allTestResult.addAll(passedTests);

            testStartTime = context.getStartDate();
            testEndTime = context.getEndDate();

            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();
            testCount = context.getAllTestMethods().length;

            methods = new LinkedList<>(Arrays.asList(context.getAllTestMethods()));

            duration = context.getEndDate().getTime() - context.getStartDate().getTime();

            allClassTestResults = groupResults(allTestResult);
            allConfigurationResults = groupResults(allConfigResult);

            parameters = context.getCurrentXmlTest().getLocalParameters();
        }

        public void removeTestedMethod(MethodResult methodResult) {
            for (int i = 0; i < methods.size(); i++) {
                if (methods.get(i).getMethodName().equals(methodResult.methodName) &&
                        methods.get(i).getTestClass().getName().equals(methodResult.getResults().get(0).getTestClass().getName())) {
                    methods.remove(methods.get(i));
                    break;
                }
            }
        }

        protected List<ClassResult> groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                resultsList.sort(RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        MethodResult methodResult = new MethodResult(previousMethodName, resultsPerMethod);
                        resultsPerClass.add(methodResult);
                        removeTestedMethod(methodResult);
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName, resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            MethodResult methodResult = new MethodResult(previousMethodName, resultsPerMethod);
                            resultsPerClass.add(methodResult);
                            removeTestedMethod(methodResult);
                            resultsPerMethod = Lists.newArrayList();

                            previousMethodName = methodName;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                MethodResult methodResult = new MethodResult(previousMethodName, resultsPerMethod);
                resultsPerClass.add(methodResult);
                removeTestedMethod(methodResult);
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName, resultsPerClass));
            }
            return classResults;
        }

        public String getTestName() {
            return testName;
        }

        public Date getTestStartTime() {
            return testStartTime;
        }

        public Date getTestEndTime() {
            return testEndTime;
        }

        public List<ClassResult> getAllClassTestResults() {
            return allClassTestResults;
        }

        public List<ClassResult> getAllConfigurationResults() {
            return allConfigurationResults;
        }

        public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public long getDuration() {
            return duration;
        }

        public int getTestCount() {
            return testCount;
        }

    }

    /**
     * Groups {@link MethodResult}s by class.
     */
    protected static class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;
        int nPassTC = 0, nFailTC = 0, nSkipTC = 0;
        long start = Long.MAX_VALUE, end = Long.MIN_VALUE;

        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
            for (MethodResult result : methodResults) {
                nPassTC += result.pass;
                nFailTC += result.fail;
                nSkipTC += result.skip;

                if (start > result.start)
                    start = result.start;

                if (end < result.end)
                    end = result.end;
            }
        }

        public String getClassName() {
            return className;
        }

        public List<MethodResult> getMethodResults() {
            return methodResults;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ClassResult) {
                return className.equals(((ClassResult) obj).className);
            }
            return false;
        }
    }

    /**
     * Groups test results by method.
     */
    protected static class MethodResult {
        private final List<ITestResult> results;
        private String methodName;
        int pass = 0, skip = 0, fail = 0;
        long start = Long.MAX_VALUE, end = Long.MIN_VALUE;

        public MethodResult(String methodName, List<ITestResult> results) {
            this.results = results;
            this.methodName = methodName;
            for (ITestResult result : results) {
                switch (result.getStatus()) {
                    case SKIP:
                        skip++;
                        break;
                    case SUCCESS:
                        pass++;
                        break;
                    case FAILURE:
                        fail++;
                        break;
                }

                if (start > result.getStartMillis())
                    start = result.getStartMillis();

                if (end < result.getEndMillis())
                    end = result.getEndMillis();

            }
        }

        public List<ITestResult> getResults() {
            return results;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MethodResult) {
                return methodName.equals(((MethodResult) obj).methodName);
            }
            return false;
        }

    }

    /* Convert long type milliseconds to format hh:mm:ss */
    public String convertTimeToString(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

}
