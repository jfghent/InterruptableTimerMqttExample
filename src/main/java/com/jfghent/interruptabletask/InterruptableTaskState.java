/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jfghent.interruptabletask;

/**
 *
 * @author jon
 */
public enum InterruptableTaskState {
    PAUSED,
    CANCELED,
    RUNNING,
    WAITING,
    CREATED,
    INITIALIZED
}