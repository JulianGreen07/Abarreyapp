package com.abarreyapp;

public class TestAddDialog {
    public static void main(String[] args) {
    // Ejecutar una prueba sin interfaz de la lógica de validación (no requiere GUI)
        AddUserDialog d = new AddUserDialog(null, "Test", null);
    d.setNameField("Prueba Usuario");
    d.setRoleSelection("Usuario");
    d.setEmailField("correo@ejemplo.com");
    d.setPhoneField("(662) 123-4567");
    d.submitProgrammatically();
        System.out.println("confirmed=" + d.isConfirmed());
        Object[] ud = d.getUserData();
        System.out.println("data: name=" + ud[0] + " role=" + ud[1] + " email=" + ud[2] + " phone=" + ud[3]);
    }
}
