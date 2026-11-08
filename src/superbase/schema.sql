create extension if not exists pgcrypto;

create table if not exists users (
    id uuid primary key default gen_random_uuid(),
    username varchar(100) not null unique,
    password_hash text not null,
    first_name text not null,
    last_name text not null,
    created_at timestamptz not null default now()
);

create table if not exists friendships (
    id uuid primary key default gen_random_uuid(),
    requested_by uuid not null references users(id) on delete cascade,
    sent_to uuid not null references users(id) on delete cascade,
    status varchar(20) not null default 'PENDING',
    added_at timestamptz,
    unique (requested_by, sent_to),
    check (requested_by <> sent_to)
);

create table if not exists groups (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    created_by uuid not null references users(id) on delete cascade,
    created_at timestamptz not null default now()
);

create table if not exists group_members (
    group_id uuid not null references groups(id) on delete cascade,
    user_id uuid not null references users(id) on delete cascade,
    primary key (group_id, user_id)
);

create table if not exists expenses (
    id uuid primary key default gen_random_uuid(),
    paid_by uuid not null references users(id) on delete cascade,
    description text not null,
    amount numeric(12, 2) not null check (amount > 0),
    created_at timestamptz not null default now()
);

create table if not exists group_expenses (
    id uuid primary key default gen_random_uuid(),
    group_id uuid not null references groups(id) on delete cascade,
    paid_by uuid not null references users(id) on delete cascade,
    description text not null,
    amount numeric(12, 2) not null check (amount > 0),
    created_at timestamptz not null default now()
);

create table if not exists expense_shares (
    expense_id uuid not null references group_expenses(id) on delete cascade,
    user_id uuid not null references users(id) on delete cascade,
    amount numeric(12, 2) not null check (amount > 0),
    primary key (expense_id, user_id)
);

create table if not exists notifications (
    id uuid primary key default gen_random_uuid(),
    sender_id uuid references users(id) on delete cascade,
    receiver_id uuid not null references users(id) on delete cascade,
    type text not null,
    payload jsonb not null default '{}'::jsonb,
    is_read boolean not null default false,
    created_at timestamptz not null default now()
);

alter table users enable row level security;
alter table friendships enable row level security;
alter table groups enable row level security;
alter table group_members enable row level security;
alter table expenses enable row level security;
alter table group_expenses enable row level security;
alter table expense_shares enable row level security;
alter table notifications enable row level security;
