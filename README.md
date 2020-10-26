# 🐍 Snake Protocol
Snake Protocol es una implementación de un protocolo que describe una comunicación cliente y servidor desarrollada en el lenguaje de programación Java, es el resultado del desarrollo de la primera practica de Telemática. Este sistema está diseñado específicamente para la transferencia de archivos donde se manejan ciertas características independientes de la aplicación y se realiza tanto un desarrollo para el cliente como para el servidor los cuales hacen parte del mismo aplicativo. El sistema cuenta con un documento en PDF donde se recopila toda la información del protocolo el cual se encuentra en este repositorio con el nombre de **practica.pdf**, adicionalmente el sistema cuenta con un diagrama de secuencias explicativo del protocolo el cual se encuentra también en este repositorio con el nombre de **diagrama.png**.

### Comandos del Cliente

 - `help` – Permite ver una lista con todos los comandos del cliente. Ejemplo de uso: _help_
- `developers` – Permite ver una lista con los desarrolladores de Snake Protocol. _Ejemplo de uso: developers_
- `load` -p <ruta-de-archivo-local> -b <bucket-remoto> -f <nombre-de-archivo-remoto> - Permite subir un archivo local a un bucket del servidor indicando el bucket y el nombre del archivo así como también la ruta del archivo local. _Ejemplo de uso: load -p C:/imagen.jpg -b bucket01 -f imagen.jpg_
- `download` -d <ruta-local-de-destino> -b <bucket-remoto> -f <nombre-de-archivo-remoto> - Permite descargar un archivo remoto de un bucket indicando el bucket, el nombre del archivo y la ruta de destino donde se guardará el archivo (incluyendo el nombre). Ejemplo de uso: _download -d C:/imagen1.jpg -b bucket01 -f imagen.jpg_
- `newb` -n <nombre-del-nuevo-bucket> – Permite crear un nuevo bucket remoto. _Ejemplo de uso: newb -n bucket01_
- `deleteb` -n <nombre-del-bucket-a-borrar> - Permite borrar un bucket existente en el servidor con todos sus contenidos. _Ejemplo de uso: deleteb -n bucket01_
- `deletef` -b <nombre-del-bucket> -n <nombre-del-archivo-a-borrar> - Permite borrar un archivo dentro de un bucket. _Ejemplo de uso: deletef -b bucket01 -n imagen.jpg_
- `buckets` – Permite listar todas las buckets que fueron creadas en el servidor. _Ejemplo de uso: buckets_
- `files` -b <nombre-del-bucket> - Permite listar todos los archivos dentro de un bucket. _Ejemplo de uso: files -b bucket01_
- `uptime` – Permite saber cuanto tiempo el servidor lleva encendido. _Ejemplo de uso: uptime_
- `close` – Cierra la conexión (de manera correcta) con el servidor y a su vez finaliza el proceso del cliente. _Ejemplo de uso: close_

### Imagenes

![Prueba Uptime (Cliente)](https://i.imgur.com/pd9ui3r.png)
![Prueba Uptime (Servidor)](https://i.imgur.com/4YumvJp.png)

### Desarrolladores
- Abraham M. Lora (ToxicSSJ)
- Camilo G. Montoya (cglv11)