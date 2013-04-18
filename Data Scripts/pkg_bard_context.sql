-- DROP PACKAGE p_pbs_context;

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
-- Returns the username set in the context, always in lowercase. Or null if not set.
--
function  get_username return varchar2;

--
-- Enables Sammple VPD in CBIP and sets the context username.
--
-- Unlike result DB or gene data, CBIP cannot have a permanent VPD on the SAMPLE table.
-- There are only a small number of operations that require VPD. So VPD must be turned
-- on before the operations and off afterwards.
--
procedure enable_cbip_sample_vpd(i_username varchar2);

--
-- Disables Sample VPD in CBIP and clears the username.
--
procedure disable_cbip_sample_vpd;

--
-- Returns true if the Sample VPD is enabled in CBIP
--
function is_cbip_sample_vpd_enabled return boolean;

end;
/


CREATE OR REPLACE package body bard_context is

--
-- package scope variables
--

v_username         varchar2(100) := null;
v_cbip_sample_vpd  boolean := false;

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

function get_username return varchar2 is
begin

	return lower(v_username);

end;

procedure enable_cbip_sample_vpd(i_username varchar2) is
begin

	set_username(i_username);
	v_cbip_sample_vpd := true;

end;

--
-- Disables Sample VPD in CBIP and clears the username.
--
procedure disable_cbip_sample_vpd is
begin

	clear_username;
	v_cbip_sample_vpd := false;

end;

--
-- Returns true if the Sample VPD is enabled in CBIP
--
function is_cbip_sample_vpd_enabled return boolean is
begin

	return v_cbip_sample_vpd;

end;

end;
/
