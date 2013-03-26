CREATE OR REPLACE package bard_context is
--
-- This package contains procedures that handle the application context.
--
-- Install this package in the PBS schema.
--

-- We are not using the application context more, because there are mulitple
-- PBS schemas in the same Oracle instances.  They cannot use the same
-- global database context.

--C_CONTEXT_NAME    constant varchar2(30) := 'CTX_PBS_SECURITY';

procedure set_username(i_username varchar2);

--
-- Removes the username from the context.
--
procedure clear_username;
--
-- Removes the username from the context.
--
function clear_username return varchar2;

--
-- Returns the username set in the context, always in lowercase. Or null if not set.
--
function  get_username return varchar2;

end;
/

CREATE OR REPLACE package body bard_context is

--
-- package scope variables
--

v_username         varchar2(100) := null;

--
-- Forward declarations
--

procedure set_username(i_username varchar2) is

	i	integer;

begin

	i := instr(i_username, '@');
	if i = 0 then
		v_username := i_username;
	else
		v_username :=  substr(i_username, 1, i-1);	-- handle email address
	end if;

end;

procedure clear_username is
begin

	v_username := null;

end;

function clear_username return varchar2 is
  old_username varchar2(100);

begin
  old_username := v_username;
	clear_username();
  return concat(lower(old_username), ' was cleared from the context');
end;

function get_username return varchar2 is
begin

	return lower(v_username);

end;

end;
/
