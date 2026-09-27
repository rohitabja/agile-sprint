# AgileSprint

AgileSprint is a small, end-to-end sprint workspace built around the existing
Keycloak OAuth2/OIDC login flow.

## Stack

- Java 27, Spring Boot 4.1.1, Spring WebFlux and reactive MongoDB
- MongoDB at `mongodb://localhost:27017/agilesprint`
- Keycloak 26 (local development realm)
- React 19 + TypeScript + Vite, MUI, React Router
- ESLint and Prettier

The backend is organized into `domain`, `repositories`, `services`, `mappers`
and `controllers`. A workspace owner is an admin; invited users are members.
The MVP supports workspace creation and invites, boards with To Do, In Progress,
Testing and Done columns, task creation/assignment/editing/moving, comments,
and a live activity stream that refreshes every five seconds while a workspace
is open.

On backend startup, the MongoDB initializer checks for and creates the
`workspaces`, `workspace_members`, `boards`, `columns`, `tasks`, `task_comments`,
and `activities` collections when they do not already exist. Existing collections
are left unchanged. MongoDB would also create these collections lazily on the
first insert, but the explicit initializer makes the database shape visible
immediately after startup.

## Run locally

Requirements: Java 27, Maven, Node.js 26.7+, and Docker Desktop.

Start MongoDB and Keycloak:

```powershell
cd <path-to-agile-sprint>
docker compose up -d --wait
```

The command waits until Keycloak has finished importing the `demo` realm and
its OIDC discovery endpoint is ready before returning. Start the backend only
after this command completes.

Start the reactive API:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-27'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
mvn -pl backend spring-boot:run
```

Start the Vite app in another terminal:

```powershell
cd <path-to-agile-sprint>\frontend
npm install
npm run dev
```

Open `http://localhost:5173` and sign in with one of the users imported into
the `demo` realm:

```text
Demo user:   demo  / demo
Realm admin: admin / Admin@123
Other users: user1, user2, user3, or user4 / User@123
```

The Keycloak administration console is at `http://localhost:8081`. Its
bootstrap administrator credentials are `admin` / `admin`; these are separate
from the `admin` user in the `demo` realm. The backend uses a session cookie for
browser login and also accepts Keycloak bearer JWTs for API clients. Vite
proxies `/api`, `/oauth2`, `/login`, and `/logout` to the backend; the backend
CORS policy accepts localhost development ports.

## API overview

All routes below require the OIDC session unless noted:

| Route | Purpose |
| --- | --- |
| `GET /api/workspaces` | List workspaces for the current user |
| `POST /api/workspaces` | Create a workspace and its default board |
| `GET/POST /api/workspaces/{id}/invites` | View members / invite a username (admin) |
| `GET/POST /api/workspaces/{id}/boards` | List or create boards (create is admin) |
| `GET/POST /api/boards/{id}/tasks` | List or create tasks |
| `PATCH /api/tasks/{id}` | Update title, description, priority or assignee |
| `POST /api/tasks/{id}/move` | Move a task between workflow columns |
| `GET/POST /api/tasks/{id}/comments` | Read or add comments |
| `GET /api/workspaces/{id}/activity` | Read the activity stream |

## Validation

```powershell
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-27'
mvn -pl backend -am test
cd frontend
npm run build
npm run lint
```

Stop local services with `docker compose down` (add `-v` to remove MongoDB
data).
