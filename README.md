# arena-core — Phase 1

A Spring Boot monolith. This is the very first milestone: prove the whole
stack (Maven → Spring Boot → embedded Tomcat → Postgres) wires together
correctly, before we add real entities, repositories, and matchmaking logic.

##Testing Github Trigger with N8N

## Project layout

```
arena-core/
├── pom.xml                          # Maven dependencies + build config
├── docker-compose.yml                # spins up Postgres locally
├── src/
│   ├── main/
│   │   ├── java/com/arena/core/
│   │   │   ├── ArenaCoreApplication.java   # app entry point
│   │   │   └── PingController.java         # first REST endpoint
│   │   └── resources/
│   │       └── application.yml             # datasource + actuator config
│   └── test/
│       └── java/com/arena/core/            # tests go here (empty for now)
```

## One-time setup

1. **Open the folder in VS Code**: `File → Open Folder...` → select `arena-core`.
   VS Code should detect the `pom.xml` and, if you have the Extension Pack
   for Java installed (you already do), automatically recognize this as a
   Maven project and start downloading dependencies in the background.

2. **Start Postgres** (you said Docker is already set up):
   ```bash
   docker compose up -d
   ```
   This starts a Postgres 16 container with a database called `arena`,
   username/password `arena`/`arena` (matches `application.yml` exactly —
   if you ever change one, change the other). Confirm it's running:
   ```bash
   docker ps
   ```
   You should see `arena-postgres` in the list.

## Running the app

Two options:

**Option A — VS Code UI:** open `ArenaCoreApplication.java`, you should see
a "Run" link above the `main` method (same as Kata files in Phase 0) —
click it.

**Option B — terminal:**
```bash
mvn spring-boot:run
```

Either way, watch the terminal/output log. You should see Spring Boot's
startup banner, then a line like:
```
Tomcat started on port 8080
```

## Proving it works

With the app running, open a browser (or use `curl`) and hit:

```
http://localhost:8080/api/ping
```

You should see: `pong from arena-core`

Then check the actuator health endpoint — this confirms Spring Boot can
actually reach the Postgres database, not just that the web server started:

```
http://localhost:8080/actuator/health
```

You should see something like:
```json
{"status":"UP"}
```

If this instead shows `"status":"DOWN"` or the app fails to start with a
connection error, the most common cause is Postgres not actually running —
double check `docker ps` shows `arena-postgres`, and that the port `5432`
isn't already in use by something else on your machine.

## What's next

Once `/api/ping` and `/actuator/health` both work, Phase 1 continues with:
- A `Player` entity + `PlayerRepository`
- A real `PlayerController` with `POST /api/players` and `GET /api/players/{id}`
- DTOs separate from the entity, `@Valid` validation
- Centralized error handling via `@ControllerAdvice`
