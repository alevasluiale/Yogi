/**
 *Submitted for verification at Etherscan.io on 2020-08-26
*/

pragma solidity >=0.4.22 <0.7.0;


contract VolumeChain {

    address private admin;

    event AddUserToSupplyChain(address indexed userAddress, uint256 indexed userType,
        string name, string realAddress, string country, string city);

    event CreateProducts(address indexed tokenAddress, address indexed sellerAddress, string name,
        string id,
        string symbol,
        uint256 total,
        uint256 length,
        uint256 width,
        uint256 height,
        uint256 productionDate,
        uint256 expirationDate);

    struct User {
        address userAddress;
        uint256 userType;
        string name;
        string realAddress;
        string country;
        string city;/*
            1 - Seller
            2 - Distributor
            3 - Warehouse
            4 - Buyer
        */
    }

    struct Product {
        string name;
        string id;
        string symbol;
        uint256 total;
        uint256 length;
        uint256 width;
        uint256 height;
        address sellerAddress;
        uint256 productionDate;
        uint256 expirationDate;
        string restrictedCountries;
    }

    constructor () public{
        admin = msg.sender;
    }

    function addUser(address userAddress, uint256 userType, string memory name,
        string memory realAddress, string memory country, string memory city) public {
        require(msg.sender == admin);
        User memory user = User(userAddress, userType, name, realAddress, country, city);
        usersMap[user.userAddress] = user;
        emit AddUserToSupplyChain(user.userAddress, user.userType, name, realAddress, country, city);
    }

    function getUser (address userAddress) public view returns (uint256 userType, string memory name,
        string memory realAddress, string memory country, string memory city){
        User memory user = usersMap[userAddress];
        return (user.userType, user.name, user.realAddress, user.country, user.city);
    }

    // function getCreatedProducts (address tokenAddress) public view returns ( Product memory product) {
    //     return createdProducts[tokenAddress]
    // }
    function getCreatedProductsFirstPart (address tokenAddress) public view returns (string memory name,
        string memory id,
        string memory symbol,
        uint256 total,
        uint256 length,
        uint256 width,
        uint256 height) {
        Product memory temporaryProduct =  createdProducts[tokenAddress];
        return (temporaryProduct.name, temporaryProduct.id, temporaryProduct.symbol, temporaryProduct.total,
        temporaryProduct.length, temporaryProduct.width, temporaryProduct.height);
    }

    function getCreatedProductsSecondPart ( address tokenAddress) public view returns (address sellerAddress,
        uint256 productionDate,
        uint256 expirationDate,
        string memory restrictedCountries) {
        Product memory temporaryProduct =  createdProducts[tokenAddress];
        return (temporaryProduct.sellerAddress, temporaryProduct.productionDate,
        temporaryProduct.expirationDate, temporaryProduct.restrictedCountries);
    }
    function getSellerName (address tokenAddress) public view returns (string memory name) {
        User memory user = usersMap[tokenAddress];
        return (user.name);
    }
    function addProducts (address tokenAddress, string memory name,
        string memory id,
        string memory symbol,
        uint256 total,
        uint256 length,
        uint256 width,
        uint256 height,
        address sellerAddress,
        uint256 productionDate,
        uint256 expirationDate,
        string memory restrictedCountries) public {
        require(usersMap[msg.sender].userType == 1);
        Product memory newProduct;
        newProduct.name = name;
        newProduct.id = id;
        newProduct.symbol = symbol;
        newProduct.total = total;
        newProduct.length = length;
        newProduct.width = width;
        newProduct.height = height;
        newProduct.sellerAddress = sellerAddress;
        newProduct.productionDate = productionDate;
        newProduct.expirationDate = expirationDate;
        newProduct.restrictedCountries = restrictedCountries;
        createdProducts[tokenAddress] = newProduct;
        emit CreateProducts(tokenAddress, msg.sender, name, id, symbol, total,length, width, height, productionDate,
            expirationDate);
    }

    mapping (address => User) public usersMap;
    mapping (address => Product) private createdProducts;
}