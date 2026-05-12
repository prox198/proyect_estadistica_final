package org.example;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class MuestreoClimaReal {

    public static void main(String[] args) {
        ArrayList<Double> todasTemperaturas = new ArrayList<>();
        ArrayList<String> fechasPoblacion = new ArrayList<>();

        try {
            System.out.println("--- Conectando a la API (Datos Reales 2016-2025) ---");

            String urlString = "https://archive-api.open-meteo.com/v1/archive?latitude=19.4285&longitude=-99.1277&start_date=2016-01-01&end_date=2025-12-31&daily=temperature_2m_max&timezone=America%2FMexico_City";

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            Scanner sc = new Scanner(url.openStream());
            StringBuilder sb = new StringBuilder();
            while (sc.hasNext()) sb.append(sc.nextLine());
            sc.close();

            JSONObject json = new JSONObject(sb.toString());
            JSONObject daily = json.getJSONObject("daily");
            JSONArray jsonFechas = daily.getJSONArray("time");
            JSONArray jsonTemps = daily.getJSONArray("temperature_2m_max");

            //  Filtrar y Guardar Población (N=920 días de verano)
            for (int i = 0; i < jsonFechas.length(); i++) {
                String fecha = jsonFechas.getString(i);
                String mes = fecha.substring(5, 7);

                if (mes.equals("06") || mes.equals("07") || mes.equals("08")) {
                    if (!jsonTemps.isNull(i)) {
                        todasTemperaturas.add(jsonTemps.getDouble(i));
                        fechasPoblacion.add(fecha);
                    }
                }
            }

            // IMPRESIÓN DE LA POBLACIÓN TOTAL (920 DATOS)
            System.out.println("\n>>> LISTADO DE LA POBLACIÓN TOTAL (N=" + todasTemperaturas.size() + ") <<<");
            System.out.println("============================================");
            System.out.printf("%-5s | %-12s | %-10s\n", "ID", "FECHA", "TEMP MÁX");
            System.out.println("--------------------------------------------");
            for (int i = 0; i < todasTemperaturas.size(); i++) {
                System.out.printf("%-5d | %-12s | %.1f °C\n", (i + 1), fechasPoblacion.get(i), todasTemperaturas.get(i));
            }
            System.out.println("============================================\n");

            //  Proceso de Muestreo Aleatorio Simple (n=276, 30%)
            int N = todasTemperaturas.size();
            int n = 276;

            ArrayList<Double> muestraValores = new ArrayList<>();
            ArrayList<String> muestraFechas = new ArrayList<>();
            Random rand = new Random();

            ArrayList<Double> tempPoblacion = new ArrayList<>(todasTemperaturas);
            ArrayList<String> tempFechas = new ArrayList<>(fechasPoblacion);

            for (int i = 0; i < n; i++) {
                int index = rand.nextInt(tempPoblacion.size());
                muestraValores.add(tempPoblacion.remove(index));
                muestraFechas.add(tempFechas.remove(index));
            }

            //  Ordenamiento Cronológico de la Muestra (Por Fecha)
            for (int i = 0; i < muestraFechas.size() - 1; i++) {
                for (int j = 0; j < muestraFechas.size() - i - 1; j++) {
                    if (muestraFechas.get(j).compareTo(muestraFechas.get(j + 1)) > 0) {
                        String fTemp = muestraFechas.get(j);
                        muestraFechas.set(j, muestraFechas.get(j + 1));
                        muestraFechas.set(j + 1, fTemp);
                        Double vTemp = muestraValores.get(j);
                        muestraValores.set(j, muestraValores.get(j + 1));
                        muestraValores.set(j + 1, vTemp);
                    }
                }
            }

            //  IMPRESIÓN DE LA MUESTRA SELECCIONADA (276 DATOS)
            System.out.println("\n>>> LISTADO DE LA MUESTRA SELECCIONADA (n=" + n + ") <<<");
            System.out.println("============================================");
            System.out.printf("%-5s | %-12s | %-10s\n", "ID", "FECHA", "TEMP MÁX");
            System.out.println("--------------------------------------------");
            for (int i = 0; i < muestraValores.size(); i++) {
                System.out.printf("%-5d | %-12s | %.1f °C\n", (i + 1), muestraFechas.get(i), muestraValores.get(i));
            }

            //  Cálculos Estadísticos
            double suma = 0;
            double maxVal = muestraValores.get(0);
            double minVal = muestraValores.get(0);

            for (int i = 0; i < muestraValores.size(); i++) {
                double v = muestraValores.get(i);
                suma += v;
                if (v > maxVal) maxVal = v;
                if (v < minVal) minVal = v;
            }

            // MEDIA
            double media = suma / n;

            // MEDIANA
            ArrayList<Double> valoresOrdenados = new ArrayList<>(muestraValores);
            Collections.sort(valoresOrdenados);
            double mediana;
            if (n % 2 == 0) {
                mediana = (valoresOrdenados.get(n / 2 - 1) + valoresOrdenados.get(n / 2)) / 2.0;
            } else {
                mediana = valoresOrdenados.get(n / 2);
            }

            // MODA
            HashMap<Double, Integer> frecuencias = new HashMap<>();
            int maxFrecuencia = 0;
            for (double v : muestraValores) {
                int count = frecuencias.getOrDefault(v, 0) + 1;
                frecuencias.put(v, count);
                if (count > maxFrecuencia) maxFrecuencia = count;
            }
            ArrayList<Double> modas = new ArrayList<>();
            for (Map.Entry<Double, Integer> entry : frecuencias.entrySet()) {
                if (entry.getValue() == maxFrecuencia) modas.add(entry.getKey());
            }
            String textoModa = (maxFrecuencia == 1) ? "Amodal" : modas.toString() + " °C (" + maxFrecuencia + " repeticiones)";

            // DISPERSIÓN
            double rango = maxVal - minVal;
            double sumaCuadrados = 0;
            for (double v : muestraValores) sumaCuadrados += Math.pow(v - media, 2);
            double varianza = sumaCuadrados / (n - 1);
            double desviacion = Math.sqrt(varianza);

            // el reporte impreso
            System.out.println("\n========================================");
            System.out.println("   REPORTE ESTADÍSTICO DE LA MUESTRA    ");
            System.out.println("========================================");
            System.out.printf("Población (N): %d días\n", N);
            System.out.printf("Muestra (n): %d días\n", n);
            System.out.println("----------------------------------------");
            System.out.printf(" Valor Máximo: %.1f °C\n", maxVal);
            System.out.printf(" Valor Mínimo: %.1f °C\n", minVal);
            System.out.printf(" Media: %.2f °C\n", media);
            System.out.printf(" Mediana: %.2f °C\n", mediana);
            System.out.printf(" Moda (si aplica): %s\n", textoModa);
            System.out.println(" Medidas de dispersión básicas:");
            System.out.printf("    Rango: %.1f °C\n", rango);
            System.out.printf("    Varianza: %.4f\n", varianza);
            System.out.printf("   Desviación estándar: %.2f °C\n", desviacion);
            System.out.println("========================================");





            // --- 6. HISTOGRAMA DE FRECUENCIAS (Gráfica en Consola) ---
            System.out.println("\n=======================================================");
            System.out.println("   GRÁFICA: HISTOGRAMA DE TEMPERATURAS (MUESTRA)       ");
            System.out.println("=======================================================");
            System.out.println("Eje Y: Rangos de Temperatura (°C) | Eje X: Frecuencia");
            System.out.println("-------------------------------------------------------");

            // Calcular los límites para los rangos (quitamos los decimales para agrupar)
            int minInt = (int) Math.floor(minVal);
            int maxInt = (int) Math.ceil(maxVal);


            int[] rangosFrecuencia = new int[maxInt - minInt + 1];

            // Contar cuántas temperaturas caen en cada cajon/intervalo pues
            for (double v : muestraValores) {
                int indice = (int) Math.floor(v) - minInt;
                rangosFrecuencia[indice]++;
            }

            // Dibujar la gráfica
            for (int i = 0; i < rangosFrecuencia.length; i++) {
                int limiteInf = minInt + i;
                int limiteSup = limiteInf + 1;

                // Etiquetas de la gráfica (Ej: [23°C - 24°C))
                System.out.printf("[%2d°C - %2d°C) | %3d días | ", limiteInf, limiteSup, rangosFrecuencia[i]);

                // Dibujar las barras ( 1 bloque = 2 días para que quepa)
                int numBloques = rangosFrecuencia[i] / 2;
                for (int a = 0; a < numBloques; a++) {
                    System.out.print("█");
                }
                // Si la frecuencia es impar, ponemos un bloque a la mitad
                if (rangosFrecuencia[i] % 2 != 0) {
                    System.out.print("▌");
                }
                System.out.println(); // Salto de línea para la siguiente barra
            }
            System.out.println("=======================================================");
            System.out.println("* Nota: Cada bloque '█' representa aprox. 2 días.");







        }catch (Exception e){
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();

        }



    }
}