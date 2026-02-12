# Integration Plan

Goal: keep the JavaFX module and the future web module aligned on data and behavior.

## Database
- Single schema: `agriflow_db`
- Keep table and column names stable to avoid breaking both modules.
- Enums must stay in sync with Java enums (type, statut).

## JavaFX (current module)
- JDBC only, no ORM.
- Singleton connection in `MyDatabase`.
- Always use `PreparedStatement`.

## Web backend (Symfony)
- Assets go in `public/` (css, js, images).
- Templates go in `templates/` with a shared `base.html.twig`.
- Use `asset()` for links to css/js/images.
- CRUD workflow:
  - `php bin/console make:entity`
  - `php bin/console make:migration`
  - `php bin/console doctrine:migrations:migrate`
  - `php bin/console make:crud`
- Database config in `.env`:
  - `DATABASE_URL="mysql://root:@127.0.0.1:3306/agriflow_db"`

## Frontend (Twig)
- Split pages using Twig blocks.
- Start with a base layout and extend it.

## Next integration steps
1. Freeze DB schema for v1.
2. Align enum labels between Java and DB.
3. Decide if JavaFX keeps direct DB access or moves to a REST API.
4. Add a shared data dictionary for field meanings and types.
