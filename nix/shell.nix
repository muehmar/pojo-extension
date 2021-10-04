let

  nixpkgs = import (import ./sources.nix).nixpkgs {};
  java = nixpkgs.openjdk8;

in

nixpkgs.mkShell {
  name = "PojoExtension";
  JAVA_HOME = "${java}/lib/openjdk";
  JDK_HOME = "${java}/lib/openjdk";

  buildInputs = with nixpkgs; [
    figlet lolcat # banner printing on enter

    java
  ];

  shellHook = ''
    figlet $name | lolcat --freq 0.5
  '';
}
