/////////////////////////////////////////////////////////////
//// molcompress.js - version 2.6
//// Molfile Compression/Decompression routines
////
//// Copyright (c) 1998-2012 ChemAxon Ltd., Peter Csizmadia
//// All Rights Reserved.
////
//// This software is free for noncommercial use.
/////////////////////////////////////////////////////////////

// Associative array of atom names and atomic numbers.
function AtomInfo() {
	var s = ",H,He,"+ //1-2
		"Li,Be,B,C,N,O,F,Ne,"+ //3-10
		"Na,Mg,Al,Si,P,S,Cl,Ar,"+ //11-18
		"K,Ca,Sc,Ti,V,Cr,Mn,Fe,Co,Ni,Cu,Zn,Ga,Ge,As,Se,Br,Kr,"+ //19-36
		"Rb,Sr,Y,Zr,Nb,Mo,Tc,Ru,Rh,Pd,Ag,Cd,In,Sn,Sb,Te,I,Xe,"+ //37-54
		"Cs,Ba,La,Ce,Pr,Nd,Pm,Sm,Eu,Gd,Tb,Dy,Ho,Er,Tm,Yb,Lu,"+ //55-71
		"Hf,Ta,W,Re,Os,Ir,Pt,Au,Hg,Tl,Pb,Bi,Po,At,Rn,"+ //72-86
		"Fr,Ra,Ac,Th,Pa,U,Np,Pu,Am,Cm,Bk,Cf,Es,Fm,Md,No,Lr,"+ //87-103
		"Rf,Db,Sg,Bh,Hs,Mt,"+ //104-109
		"L,LP,A,Q,*,R#"; //110-115
	s = s.split(",");
	var i;
	for(i=s.length-1; i>=0; --i) {
		if(s[i] != "") {
			this[s[i]] = i;
			this[i] = s[i];
		}
	}
}
atoms = new AtomInfo();

// Equivalent to printf("%3d",x)
function mitoa(x) {
	var s = "  "+x.toString();
	return s.substring(s.length-3);
}

///////////////////////
//// compression only
///////////////////////

// Convert an integer into reverse base-64.
function mito6(i, n) {
	var s = "";
	var j;
	for(j=0; j < n; ++j) {
		var k = i&63;
		s += "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-".charAt(k);
		i >>= 6;
	}
	return s;
}

///////////////////////
//// decompression only
///////////////////////

// Inverse array of base-64 digits.
function Digits64Object() {
	var i;
	for(i=0; i < 64; ++i) {
		this["0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-".charAt(i)] = i;
	}
}

var digits64u = new Digits64Object();

// Read reverse base-64 integer.
function m6toi(s, l, m) {
	var i = 0;
	var j;
	for(j=l; j < m; ++j) {
		var c = s.charAt(j);
		i |= digits64u[c] << ((j-l)*6);
	}
	return i;
}

// Equivalent to printf("%10.4f",x)
function mftoa(x) {
	var y = Math.abs(x);
	var a = Math.round(y-0.5);
	var b = Math.round(10000.0*(y-a));
	var sb = "0000"+b.toString();
	var r = a.toString()+"."+sb.substring(sb.length-4);
	r = (x < 0.0)? "    -"+r : "     "+r;
	r = r.substring(r.length-10);
	return r;
}

/////////////////////////////////////////
/////// Compression/Decompression ///////
/////////////////////////////////////////

function molCompress(str, compress) {
	str = str + ""; //MICROSOFT BUG WORKAROUND
	var M;
	var has_r = str.lastIndexOf('\r') >= 0;
	var has_n = str.lastIndexOf('\n') >= 0;
	if(has_r || has_n) {
		var v;
		if(has_r) {
			M = str.split("\r");
			if(has_n) {
				M = M.join("").split("\n");
			}
		} else {
			M = str.split("\n");
		}
	} else {
		return null; // bad format: molfile with only one line
	}
	if(M.length < 5) { // bad format
		return null;
	}
	var A = 10000.0;
	var B = 8388608.0;
	var r = new String(M[0]+"\n"+M[1]+"\n"+M[2]+"\n"+M[3]+"\n");
	var na = parseInt(M[3].substring(0,3));
	var nb = parseInt(M[3].substring(3,6));
	var nchg = 0;
	var nrad = 0;
	var chg = new Array;
	var rad = new Array;
	var i;
	for(i=0; i < na; ++i) {
		rad[i] = 0;
		chg[i] = 0;
	}
	var hasmchg = false;
	var hasmrad = false;
	for(i=4+na+nb; i < M.length; ++i) {
		if(M[i].substring(0,6)=="M  CHG") {
			var n = parseInt(M[i].substring(6,9));
			var j;
			for(j=0; j < n; ++j) {
				var k = 9 + 8*j;
				var a = parseInt(M[i].substring(k,k+4));
				chg[a-1] = parseInt(M[i].substring(k+4,k+8));
				if(chg[a-1] != 0) {
					++nchg;
				}
			}
			hasmchg = true;
		}
		if(M[i].substring(0,6)=="M  RAD") {
			var n = parseInt(M[i].substring(6,9));
			var j;
			for(j=0; j < n; ++j) {
				var k = 9 + 8*j;
				var a = parseInt(M[i].substring(k,k+4));
				rad[a-1] = parseInt(M[i].substring(k+4,k+8));
				if(rad[a-1] != 0) {
					++nrad;
				}
			}
			hasmrad = true;
		}
	}

	// atom block
	for(i=0; i < na; ++i) {
		var s = M[4+i];
		if(s.length == 10 || s.length == 14) { // compressed line
			if(compress) {
				r += s+"\n";
			} else {
				r += mftoa((m6toi(s,0,4)-B)/A)+mftoa((m6toi(s,4,8)-B)/A);
				var offset = 8;
				if(s.length == 14) { // 3d
					r += mftoa((m6toi(s,8,12)-B)/A);
					offset += 4;
				} else { // 2d
					r += "    0.0000";
				}
				r += " ";
				var a = m6toi(s, offset, offset+2);
				var atsym = atoms[a&127];
				var stereo = (a>>7)&3;
				r += (atsym + "   ").substring(0,3) + " 0  ";
				if(chg[i] != 0) {
					r += (4-chg[i]).toString();
				} else if(rad[i] == 2) { // doublet
					r += "4"
				} else {
					r += "0"
				}
				r += mitoa(stereo) + "  0  0  0  0  0  0  0  0  0\n";
			}
		} else { // uncompressed line
			if(compress) {
				var unknown = false;
				var x = Math.round(parseFloat(s.substring(0,10))*A+B);
				var y = Math.round(parseFloat(s.substring(10,20))*A+B);
				var sz = s.substring(20,30);
				var z = Math.round(parseFloat(sz)*A+B);
				var rr = mito6(x,4) + mito6(y,4);
				if(sz != "    0.0000") { // 3d
					rr += mito6(z,4);
				}
				var atsym = s.substring(31,34);
				var j;
				for(j=0; j < atsym.length; ++j) {
					if(atsym.charAt(j)==' ') {
						atsym = atsym.substring(0,j);
						break;
					}
				}
				var atno = atoms[atsym];
				if(atno == null) { // unknown atom type
					atno = 0;
					unknown = true;
				}
				var stereo = parseInt(s.substring(39,42));
				if(stereo < 0 || stereo > 3) {
					unknown = true;
				}
				var a = atno & 127; // 7 bit
				a |= (stereo & 3) << 7; // 2 bit
				rr += mito6(a,2);
				var chargecode = parseInt(s.substring(36,39));
				if(chargecode == 4) {
					if(rad[i] == 0) {
						++nrad;
						rad[i] = 2;
					}
				} else if(chargecode != 0 && chg[i] == 0) {
					++nchg;
					chg[i] = 4 - chargecode;
				}

				// line containing unknown fields
				// cannot be compressed
				if(s.substring(34,36) != " 0") {
					unknown = true;
				} else if(!unknown) {
					for(j=42; j < 56; ++j) {
						var c = s.charAt(j);
						if(c != ' ' && c != '0') {
							unknown = true;
							break;
						}
					}
					// Column 56 might contain number 2 if
					// the file was written by ChemDraw.
					// This number has no information
					// content, it should not prevent the
					// line from being compressed.
					// (cspeter 2009.05.12.)
					for(j=57; j < s.length; ++j) {
						var c = s.charAt(j);
						if(c != ' ' && c != '0') {
							unknown = true;
							break;
						}
					}
				}
				r += (unknown? s : rr)+"\n";
			} else {
				r += s+"\n";
			}
		}
	}

	// bond block
	for(i=0; i < nb; ++i) {
		var s = M[4+na+i];
		if(s.length == 5) { // compressed
			if(compress) {
				r += s+"\n";
			} else {
				var i1 = m6toi(s,0,2);
				var i2 = m6toi(s,2,4);
				var t = m6toi(s,4,5);
				var type = t&7;
				var stereo = (t>>3)&7;
				if(type == 0) {
					type = 8;
				}
				r += mitoa(i1)+mitoa(i2)+mitoa(type)+
				     mitoa(stereo)+"  0  0  0\n";
			}
		} else {
			if(compress) {
				var unknown = false;
				var i1 = parseInt(s.substring(0, 3));
				var i2 = parseInt(s.substring(3, 6));
				var type = parseInt(s.substring(6, 9));
				var stereo = parseInt(s.substring(9, 12));
				if(type < 1 || type>8 || stereo!=(stereo&7)) {
					unknown = true;
				}
				stereo &= 7;
				var t = (type & 7) | (stereo << 3);
				var rr = mito6(i1,2)+mito6(i2,2)+mito6(t,1);

				// line containing unknown fields
				// cannot be compressed
				if(!unknown) {
					var j;
					for(j=12; j < s.length; ++j) {
						var c = s.charAt(j);
						if(c != ' ' && c != '0') {
							unknown = true;
							break;
						}
					}
				}
				r += (unknown? s : rr)+"\n";
			} else {
				r += s+"\n";
			}
		}
	}
	if(nchg!=0 && !hasmchg) {
		r += "M  CHG"+mitoa(nchg);
		for(i=0; i < na; ++i) {
			if(chg[i] != 0) {
				r += " "+mitoa(i+1);
				r += ((chg[i] < 0)? "  ":"   ")+chg[i];
			}
		}
		r += "\n";
	}
	if(nrad!=0 && !hasmrad) {
		r += "M  RAD"+mitoa(nrad);
		for(i=0; i < na; ++i) {
			if(rad[i] != 0) {
				r += " "+mitoa(i+1);
				r += ((rad[i] < 0)? "  ":"   ")+rad[i];
			}
		}
		r += "\n";
	}
	for(i=4+na+nb; i < M.length-1; ++i) {
		r += M[i]+"\n";
	}
	if(4+na+nb < M.length)
		r += M[M.length-1];
	return r;
}
