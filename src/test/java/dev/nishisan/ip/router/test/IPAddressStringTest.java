/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dev.nishisan.ip.router.test;

import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;

/**
 *
 * @author Lucas Nishimura <lucas.nishimura at gmail.com>
 * created 01.10.2024
 */
public class IPAddressStringTest {

    public static void main(String[] args) throws AddressStringException {
        IPAddressString a = new IPAddressString("192.168.1.10/24");
        System.out.println(":::" + a.getAddress().toPrefixBlock());
    }
}
