# Contributing

This guide covers how to work on an issue and how to review a teammate's pull request.
Follow the steps in order, and read the warnings in bold.

---

## Starting a new issue

### 1. Open your assigned issue

- Click on the issue **assigned to you**.
- Finish issues **in order of the weeks**.
- **Do not click on or work on anyone else's issue.**

### 2. Create a branch from the issue

1. On the right-hand side of the issue, click **Create a branch**.
2. A pop-up appears with two commands. Copy and paste them into your **IntelliJ terminal**. They fetch the latest `main` and create your new branch.

Example (your branch name will be different):

```bash
git fetch origin
git checkout 12-add-expense-entity
```

### 3. Stage and commit regularly

- Stage your files often so your work is saved.
- Start every commit message with one of these prefixes:

| Prefix | Use it for | Example |
|---|---|---|
| `add/` | New classes, files, or features | `add/ExpenseShare entity` |
| `fix/` | Fixing something that was working incorrectly | `fix/split calculation rounding` |
| `test/` | Adding or changing tests | `test/AddExpenseInteractor` |
| `bug/` | Fixing a reported bug | `bug/login crashes on empty username` |

Use another clear prefix if none of these fit (for example `docs/` or `refactor/`).

### 4. Push your branch

Run `git push` so your branch is available on the repository.

If Git says the branch has no upstream yet, run the command it suggests:

```bash
git push --set-upstream origin <your-branch-name>
```

### 5. Create a Pull Request

> ⚠️ **MAKE SURE YOU ARE REQUESTING TO MERGE FROM YOUR BRANCH INTO `main` ON OUR REPOSITORY, NOT THE CSC207 REPOSITORY THIS PROJECT WAS FORKED FROM.**

Before you click **Create pull request**, check the **base repository** and **base branch** dropdowns at the top of the page. The base repository must be **our team repository**, and the base branch must be `main`.

> 📸 **Screenshot placeholder (should do):** the PR page with the base repository set to our repository and `main` selected.
> Suggested file: `docs/images/create-pr-do.png`

> 📸 **Screenshot placeholder (should NOT do):** the PR page with the base repository set to the original CSC207 repository.
> Suggested file: `docs/images/create-pr-dont.png`

---

## Reviewing a pull request

### 1. Assign yourself

On someone else's pull request, under **Reviews**, click **assign yourself**.

### 2. Read the context

Open the **Conversation** tab and read the PR description for details on what the change does.

### 3. Check the tests

Open the **Checks** tab to see which tests are failing.

### 4. Review the code

- Go through the commits and the changed files.
- To leave a comment, click the **plus icon** next to the line of code you want to comment on.

### 5. Finish your review

When you are done, click **Finish your review**. Add a short summary of the comments you left, then submit.
