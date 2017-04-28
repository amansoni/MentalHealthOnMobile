package com.example.theom.mmha.Test;

import com.example.XML_parser;
import com.example.theom.mmha.Assessment.XML_parser;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Theo on 27/04/2017.
 */

public class Tests {
    //for normal addition
    @Test
    public void testQuestionText()
    {
        NodeList nList = org.apache.harmony.xml.dom.ElementImpl@370326e;
        String questionCode = "suic-cur-int";
        String questionText = "Do you have reason to be concerned about the person’s current intention to complete suicide?";
        assertEquals(questionText, XML_parser.getQuestionText(nList, questionCode);
    }

}
