# Branch Protection

`main` is protected. You cannot push to it directly, and you cannot merge
without an approving review. This document records what is
configured and how to change it.

## Rules on `main`

- Require a pull request before merging.
- Require 1 approving review.
- Require branches to be up to date before merging.
- Require linear history.
- Block force pushes.
- Block branch deletion.

## Merge Settings

At `Settings -> General -> Pull Requests`:

- Squash merging enabled.
- Merge commits disabled.
- Rebase merging disabled.
- Automatically delete head branches enabled.

Squash-only keeps history on `main` to one commit per pull request, which is
what "require linear history" expects.

## Changing These

`Settings -> Branches -> Branch protection rules`, then edit the `main` rule.
These are repository settings, not files, so they are not version controlled and
a change takes effect immediately for everyone. Talk to the team before
loosening anything.

If you add a new required status check, the workflow has to have run at least
once before GitHub will offer its name in the selector.
