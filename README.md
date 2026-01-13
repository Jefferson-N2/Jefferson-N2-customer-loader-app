# 🚀 Customer Loader App

## Descripción
Plataforma de carga masiva de datos de nómina. Permite cargar archivos TXT con información de empleados, validarlos y crear cuentas bancarias automáticamente.

## Stack
- **Backend:** Java 21, Wildfly, Jakarta EE, MySQL
- **Frontend:** Angular 21+, TypeScript
- **Infraestructura:** Docker, Docker Compose

## Ejecutar la Aplicación

### Requisitos
- Docker & Docker Compose 
- Puerto 8081 (Backend), 3306 (BD), 4200 (Frontend)

### Levantar los servicios
```bash
docker-compose up -d
```

### Verificar que todo está funcionando
```bash
# Health check
curl http://localhost:8081/health
```

### Acceder a la aplicación
- **Frontend:** http://localhost:4200
- **Backend API:** http://localhost:8081
- **Swagger-docs:** http://localhost:8081/customer-loader-backend/api/openapi

## Archivos de Prueba

### Formato del archivo TXT
```
C|1725364578|2026-01-09|800|jaime123@gmail.com|0954887845
P|A123|2026-01-07|700|jose123@gmail.com|0954887842
```

Los campos son:
- **Tipo ID:** C (Cédula) o P (Pasaporte)
- **Número ID:** Alfanumérico
- **Fecha:** yyyy-MM-dd
- **Valor:** Numérico
- **Email:** Formato válido
- **Teléfono:** 10 dígitos

```bash
# Subir archivo de prueba
curl -X POST http://localhost:8081/bulk-load/clients \
  -H 'Content-Type: application/octet-stream' \
  --data-binary '@scripts/sample_bulk_load.txt'
```

## Estructura del Proyecto

```
customer-loader-app/
├── backend/
│   └── customer-loader-backend/
│       ├── src/main/java/com/corporate/payroll/
│       │   ├── domain/ (Validaciones, modelos)
│       │   ├── application/ (Casos de uso)
│       │   ├── adapter/ (REST, BD, servicios)
│       │   └── port/ (Interfaces)
│       └── src/test/java/ (26 tests)
│
├── frontend/
│   └── customer-loader-frontend/
│       └── src/app/
│           ├── pages/bulk-load/ (Upload)
│           ├── services/ (HTTP)
│           └── models/ (DTOs)
│
└── scripts/
    ├── database.sql (Schema)
    └── sample_bulk_load.txt (Test data)
```

## Iniciar Servicios Backend en Local

```bash
cd backend/customer-loader-backend

# Compilar
./mvnw clean install
```

El backend se puede deployar en Wildfly.

## Ejecutar Tests
```bash
cd backend/customer-loader-backend

# Usando el wrapper
./mvnw test

# O con cobertura
./mvnw test jacoco:report
```

## Base de Datos
```bash
# Acceder a MySQL
mysql -h 127.0.0.1 -u root -proot payroll_db

# Ver clientes creados
SELECT * FROM clients;
```

## Detener servicios
```bash
docker-compose down
```

---

mysql -h 127.0.0.1 -u root -proot payroll_db

# Ver clientes creados
SELECT * FROM clients;
```

---


