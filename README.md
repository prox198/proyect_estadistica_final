# proyect_estadistica_final


1. Descripción del Proyecto
Este programa en Java se conecta a la API de Open-Meteo para obtener datos históricos de temperaturas máximas en la Ciudad de México (2016-2025). El objetivo es aplicar técnicas de Muestreo Aleatorio Simple para analizar el comportamiento estadístico del verano.

2. Metodología
Población (N): 920 días de verano detectados en la última década.

Muestra (n): 276 días (30% de la población).

Técnica: Muestreo sin reemplazo para garantizar que cada dato sea único.

3. Justificación del Histograma
Se eligió el histograma con una amplitud de 1°C para evitar la saturación de datos que causarían las gráficas de pastel o de barras simples. Esto permite agrupar los 276 valores en intervalos claros y legibles.

4. Cómo ejecutar el programa
Asegúrate de tener instalada la librería org.json (gestionada vía Maven en el archivo pom.xml).

Ejecuta la clase MuestreoClimaReal.java.

El programa imprimirá automáticamente la tabla de población, la muestra seleccionada, los cálculos de tendencia central y el histograma horizontal.


6. Interpretación de resultados
Los resultados indican que el clima de verano en la Ciudad de México es predominantemente templado, con una concentración de temperaturas máximas que oscila entre los 22°C y 26°C.

La forma de la "montaña" en nuestro histograma muestra una distribución unimodal (con un pico claro), lo que sugiere estabilidad climática: los días extremadamente fríos o extremadamente calurosos son raros (casos aislados en las orillas de la gráfica).
