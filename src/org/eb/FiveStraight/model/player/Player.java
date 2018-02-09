/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eb.FiveStraight.model.player;

/**
 *
 * @author A403163
 */
public interface Player {
  void setName(String name);
  String getName();
  int makeMove();
  
}
