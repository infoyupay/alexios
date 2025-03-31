/**
 * This is the main explicit module of Alexios.
 *
 * @author InfoYupay SACS
 */
module alexios.main {
    requires google.api.client;
    requires com.google.api.client;
    requires com.google.api.client.json.gson;
    requires com.google.gson;
    requires com.google.api.client.auth;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires org.jetbrains.annotations;
    requires com.google.api.services.sheets;
    requires com.google.api.services.drive;

    opens org.yupay.alexios.api;
}
