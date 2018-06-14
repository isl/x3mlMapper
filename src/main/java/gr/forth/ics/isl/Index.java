/*
 * Copyright 2015 Institute of Computer Science,
 * Foundation for Research and Technology - Hellas
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 *
 * Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
 * Tel:+30-2810-391632
 * Fax: +30-2810-391638
 * E-mail: isl@ics.forth.gr
 * http://www.ics.forth.gr/isl
 *
 * Authors :  Georgios Samaritakis.
 *
 * This file is part of the x3mlMapper webapp.
 */
package gr.forth.ics.isl;

import eu.delving.x3ml.X3MLEngine;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.output.WriterOutputStream;

/**
 *
 * @author samarita
 */
public class Index extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String id = request.getParameter("id");
        String sourceFile = request.getParameter("sourceFile");
        String thesaurus = request.getParameter("thesaurus");
        String generator = request.getParameter("generator");
        String uuidSize = request.getParameter("uuidSize");
        String outputFormat = request.getParameter("output");

        String editorName = getServletContext().getInitParameter("editorName");
        int uuidSizeInt = 2;

        if (uuidSize != null) {
            try {
                uuidSizeInt = Integer.parseInt(uuidSize);
            } catch (NumberFormatException ex) {

            }
        }

        Mapper map = new Mapper();

        try {
            String serverIP = request.getLocalAddr();

            if (serverIP.equals("0:0:0:0:0:0:0:1")) {//Localhost
                serverIP = "localhost";
            }
            X3MLEngine engine;
            String x3mlURL = "http://" + serverIP + ":" + request.getLocalPort() + "/" + editorName + "/Services?id=" + id + "&output=text/xml&method=export";

            if (thesaurus == null || thesaurus.length() == 0) {
                engine = map.engine(x3mlURL);
            } else {
                engine = map.engine(x3mlURL, thesaurus);
            }

            if (engine == null) {             
                String errors = map.getEngineErrors().toString();
                out.println("An error ocurred while validating X3ML mappings file: "+errors);
            } else {
                X3MLEngine.Output output = engine.execute(map.documentFromString(sourceFile), map.policy(generator, uuidSizeInt));
                if (X3MLEngine.exceptionMessagesList.length() > 0) {
                    out.println(X3MLEngine.exceptionMessagesList.replaceAll("eu\\.delving\\.x3ml\\.X3MLEngine\\$X3MLException:\\s", "\n").trim());
                }
                if (outputFormat == null || outputFormat.equals("RDF/XML")) {
                    StringWriter tempWriter = new StringWriter();
                    OutputStream os = new WriterOutputStream(tempWriter, "UTF-8");
                    PrintStream ps = new PrintStream(os);
                    output.writeXML(ps);

                    String decoded = replaceUnicode(tempWriter.toString());
                    out.println(decoded);

                } else if (outputFormat.equals("N-triples")) {
                    out.println(replaceUnicode(output.toString()));
                } else if (outputFormat.equals("Turtle")) {

                    StringWriter tempWriter = new StringWriter();
                    OutputStream os = new WriterOutputStream(tempWriter, "UTF-8");
                    PrintStream ps = new PrintStream(os);
                    output.write(ps, "text/turtle");

                    String decoded = replaceUnicode(tempWriter.toString());
                    out.println(decoded);

                }
            }

        } catch (X3MLEngine.X3MLException ex) {
            System.out.println("x3mlEx");
            ex.printStackTrace();
//            System.out.println(ex.getMessage());
//            ex.printStackTrace(out);
            out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println("ex");
            ex.printStackTrace();
            out.println(ex.getMessage());

        }

        out.close();

    }

    private String replaceUnicode(String data) {
        Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
        Matcher m = p.matcher(data);
        StringBuffer buf = new StringBuffer(data.length());
        while (m.find()) {
            String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
            m.appendReplacement(buf, Matcher.quoteReplacement(ch));
        }
        m.appendTail(buf);
        return buf.toString();

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
