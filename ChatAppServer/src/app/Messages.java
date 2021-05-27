/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app;

/**
 *
 * @author MERT
 */

public class Messages implements java.io.Serializable {
    
    public static enum Message_Type {None, Name, Disconnect,RivalConnected, Text,Textt, Text3,Text4,Text5,
    Text6, Text7, Text8, Text9, Text10, Text11, Text12, Text13, Text14, Text15, Text16, Text17, Text18, Selected, Bitis,Start}
    public Message_Type type;
    public Object content;
   
    public Messages(Message_Type t)
    {
        this.type=t;
    }
 

    
    
}
