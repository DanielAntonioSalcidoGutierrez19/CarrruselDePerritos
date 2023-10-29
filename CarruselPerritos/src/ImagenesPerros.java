import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.google.gson.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
public class ImagenesPerros {
    private String apiUrl = "https://dog.ceo/api/breeds/image/random/";
    private List<String> urls;
    private int indice = 0;
    private JLabel etiqueta;
    private Timer temporizador;
    private Thread thread;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ImagenesPerros());
    }
    public ImagenesPerros() {
        JFrame frame = new JFrame("Carrusel de Perritos");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        etiqueta = new JLabel();
        frame.add(etiqueta);
        JButton boton = new JButton("Seleccionar Raza");
        boton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                seleccionarRaza();
            }
        });
        frame.add(boton, BorderLayout.SOUTH);
        int trancision = 5000;
        temporizador = new Timer(trancision, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarSiguienteImagen();
            }});
        temporizador.start();
        frame.setVisible(true); }
    private void seleccionarRaza() {
        String raza = JOptionPane.showInputDialog("Introduce la raza (por ejemplo: bulldog): ");
        if (raza != null && !raza.isEmpty()) {
            apiUrl = "https://dog.ceo/api/breed/" + raza + "/images/random/";
            indice = 0;
            thread = new Thread(() -> cargarImagenes());
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace(); }}}
    private void cargarImagenes() {
        try {
            URL url = new URL(apiUrl + obtenerNumeroAleatorio(10, 20));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == 200) {
                BufferedReader lector = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder respuesta = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null) {
                    respuesta.append(linea);}
                lector.close();
                Gson gson = new Gson();
                JsonObject respuestaJson = gson.fromJson(respuesta.toString(), JsonObject.class);
                if (respuestaJson.has("message")) {
                    JsonElement mensaje = respuestaJson.get("message");
                    if (mensaje.isJsonArray()) {
                        JsonArray arregloImagenes = mensaje.getAsJsonArray();
                        urls = gson.fromJson(arregloImagenes, List.class);
                        descargarImagenesACarpeta(urls); }}}
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace(); }}
    private void mostrarSiguienteImagen() {
        if (urls != null && urls.size() > 0) {
            try {
                URL urlImagen = new URL(urls.get(indice));
                InputStream flujoImagen = urlImagen.openStream();
                ImageIcon icono = new ImageIcon(ImageIO.read(flujoImagen));
                etiqueta.setIcon(icono);
                indice = (indice + 1) % urls.size();
            } catch (IOException e) {
                e.printStackTrace(); }}}
    private int obtenerNumeroAleatorio(int minimo, int maximo) {
        return minimo + (int) (Math.random() * (maximo - minimo + 1)); }
    private void descargarImagenesACarpeta(List<String> urls) {
        try {
            File carpeta = new File("imagenes");
            if (!carpeta.exists()) {
                carpeta.mkdir();}
            for (int i = 0; i < urls.size(); i++) {
                URL urlImagen = new URL(urls.get(i));
                InputStream flujoImagen = urlImagen.openStream();
                String nombreArchivo = "imagen" + i + ".jpg";
                File archivoImagen = new File("imagenes/" + nombreArchivo);
                ImageIO.write(ImageIO.read(flujoImagen), "jpg", archivoImagen);
                flujoImagen.close(); }
        } catch (IOException e) {
            e.printStackTrace(); }}}