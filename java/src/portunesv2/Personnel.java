/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package portunesv2;

/**
 *
 * @author bilal
 */
public class Personnel {
 
   private Long id;
   private String text;
   private Personnel nextMessage;
   
   private Personnel() {
   
   }
   
   
   public Personnel(String text) {
      this.text = text;
   }
   public Long getId() {
      return id;
   }
   private void setId(Long id) {
      this.id = id;
   }
   public String getText() {
      return text;
   }
   public void setText(String text) {
      this.text = text;
   }
   public Personnel getNextMessage() {
      return nextMessage;
   }
   public void setNextMessage(Personnel nextMessage) {
      this.nextMessage = nextMessage;
   }
   
}

