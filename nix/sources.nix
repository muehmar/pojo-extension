{
  nixpkgs = builtins.fetchGit {
    url = "https://github.com/NixOS/nixpkgs.git";
    ref = "nixos-21.11";
    rev = "d887ac7aee92e8fc54dde9060d60d927afae9d69";
  };
}
