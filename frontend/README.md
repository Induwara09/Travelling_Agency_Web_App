# Serendib Trails React Frontend

React 18 + Vite frontend for the Spring Boot API in the sibling `travel/`
folder.

## Run

```powershell
cd frontend
Copy-Item .env.example .env
npm install
npm run dev
```

Open `http://localhost:5173`.

The default API URL is `http://localhost:8080`. Change `VITE_API_BASE_URL` in
`.env` only when the backend runs elsewhere.

## Included routes

### Customer

- `/` — animated home page
- `/destinations`, `/destinations/:id`
- `/packages`, `/packages/:id`
- `/hotels`, `/hotels/:id`
- `/login`, `/register`
- `/booking/:packageId`, `/my-bookings`

### Admin

- `/admin/dashboard`
- `/admin/destinations`
- `/admin/packages`
- `/admin/hotels`
- `/admin/bookings`
- `/admin/users`

Theme preference and JWT session are stored in browser local storage. The API
still enforces every ADMIN/CUSTOMER authorization rule on the server.
