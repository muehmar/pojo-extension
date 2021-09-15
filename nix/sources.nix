{
  nixpkgs = builtins.fetchGit {
    url = "https://github.com/NixOS/nixpkgs.git";
    ref = "nixos-21.05";
    rev = "0b8b127125e5271f5c8636680b6fe274844aaa9d";
  };
}
