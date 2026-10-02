# Migración a la base de datos nueva

| Hoy | Nuevo |
|---|---|
| Base `qa`, esquema `qa` | Base **`tgv_erp`**, esquema **`erp`** (ensayo: `tgv_erp_ensayo`) |

| Archivo | Qué hace |
|---|---|
| `01_esquema.sql` | Crea el esquema limpio: mismas tablas, columnas y tipos que hoy + modelo multi-cliente (clientes, servicios_tipos, destinos, bultos). IDENTITY propia por tabla, FK validadas, índices. |
| `02_traspaso.sql` | Copia los datos desde `legacy` manteniendo los ids, limpia (textos mal codificados, comunas, RUT de proveedores) y carga el modelo multi-cliente (cliente SLEP Chiloé 61.981.360-K). |
| `03_validacion.sql` | Compara filas, ids, totales, estructura y secuencias. Termina con "TODO OK" o "HAY ERRORES". |
| `migrar.sh` | Ejecuta todo en orden en el servidor: respaldo → base nueva → restaurar como `legacy` → 01 → 02 → 03. |
| `tgv-erp.service` | Servicio systemd para el backend de producción (arranque automático y reinicio si se cae). |

Los `.sql` usan `__ESQUEMA__` como nombre del esquema; `migrar.sh` lo reemplaza (por defecto `erp`).

## 1. Copiar al servidor
En PowerShell:
```powershell
scp -r C:\Users\Victor\Documents\SpringBoot\erp\db\nueva_bd root@31.97.175.15:/root/
```
No editar `migrar.sh` con el Bloc de notas (rompe los saltos de línea). Si pasa: `sed -i 's/\r$//' migrar.sh`

## 2. Ensayo (repetirlo las veces que haga falta)
En el servidor, como root:
```bash
cd /root/nueva_bd
bash migrar.sh qa qa tgv_erp_ensayo
#              │  │  └ base nueva (el esquema nuevo es "erp"; se puede indicar como 4° argumento)
#              │  └ esquema de origen
#              └ base de origen
```
La base `qa` no se modifica. El respaldo queda en `/var/backups/tgv/`.
Para borrar el ensayo: `runuser -u postgres -- dropdb tgv_erp_ensayo`

Luego levantar el backend apuntando al ensayo (ver punto 4) y probar las pantallas principales:
órdenes, rutas, entregas, recepción desde la app, ingresos, saldos, PDF de OS, dashboard.

## 3. Día del cambio (producción)
Producción: `/home/servicios/transportes-gomez`, puerto 8081 (nginx → `transportesgv.cl`).
Corte estimado: 5 a 10 minutos. Hacerlo en un horario sin repartos.

**Antes (en tu Windows):** compilar el jar de la rama `multicliente` y subirlo con otro nombre:
```powershell
cd C:\Users\Victor\Documents\SpringBoot\erp
.\mvnw clean package -DskipTests
scp target\transportes-gomez.jar root@31.97.175.15:/home/servicios/transportes-gomez/transportes-gomez.jar.nuevo
scp db\nueva_bd\tgv-erp.service root@31.97.175.15:/etc/systemd/system/
```
(Si el jar que genera Maven tiene otro nombre, usar ese.)

**En el servidor, como root:**
```bash
cd /home/servicios/transportes-gomez

# 1. Respaldar jar y configuración actuales
cp -p transportes-gomez.jar transportes-gomez.jar.antes-fase0
cp -p application.properties application.properties.antes-fase0

# 2. Detener producción
pkill -f /home/servicios/transportes-gomez/transportes-gomez.jar; sleep 5
pgrep -af /home/servicios/transportes-gomez/transportes-gomez.jar || echo "detenido"

# 3. Migrar (la base qa no se modifica)
cd /root/nueva_bd && bash migrar.sh qa qa tgv_erp
#    -> si NO dice "TODO OK": saltar a "Volver atrás"

# 4. Configuración nueva y jar nuevo
cd /home/servicios/transportes-gomez
sed -i -e 's#^spring.datasource.url=.*#spring.datasource.url=jdbc:postgresql://localhost:5432/tgv_erp#' \
       -e 's#^spring.jpa.properties.hibernate.default_schema=.*#spring.jpa.properties.hibernate.default_schema=erp#' application.properties
grep -E "datasource.url|default_schema" application.properties
mv transportes-gomez.jar.nuevo transportes-gomez.jar

# 5. Levantar como servicio (se reinicia solo si el servidor se reinicia)
systemctl daemon-reload
systemctl enable --now tgv-erp
journalctl -u tgv-erp -f        # esperar "Started ErpApplication"; salir con Ctrl+C
```
Revisar: `transportesgv.cl`, órdenes, ruta del día, dashboard, PDF de una OS.

**Volver atrás** (el mismo día; lo registrado en la base nueva se perdería):
```bash
systemctl stop tgv-erp
cd /home/servicios/transportes-gomez
cp -p transportes-gomez.jar.antes-fase0 transportes-gomez.jar
cp -p application.properties.antes-fase0 application.properties
systemctl start tgv-erp
```

**Después de unas semanas estable:** `runuser -u postgres -- psql -d tgv_erp -c "DROP SCHEMA legacy CASCADE"`

## 4. Configuración del backend
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/tgv_erp
spring.jpa.properties.hibernate.default_schema=erp
```
(con `tgv_erp_ensayo` para el ensayo). El backend ya no tiene el esquema escrito en sus consultas:
cada conexión usa `SET search_path` con el `default_schema` configurado, y Flyway usa el mismo esquema.
