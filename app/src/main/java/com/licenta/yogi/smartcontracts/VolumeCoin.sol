pragma solidity >=0.4.22 <0.7.0;

contract ProductToken {
    string public name;
    string public symbol;
    uint8 public constant decimals = 18;
    uint256 totalSupplyVar;
    event Approval(address indexed tokenOwner, address indexed spender, uint tokens);
    event Transfer(address indexed from, address indexed to, uint tokens, uint256 indexed timestamp);
    mapping(address => uint256) balances;
    mapping(address => mapping (address => uint256)) allowed;
    using SafeMath for uint256;
    constructor(uint256 _totalSupply, string memory _name, string memory _symbol) public {
        totalSupplyVar = _totalSupply;
        name = _name;
        symbol = _symbol;
        balances[msg.sender] = _totalSupply;
    }
    function totalSupply() public view returns (uint256) {
        return totalSupplyVar;
    }

    function balanceOf(address tokenOwner) public view returns (uint) {
        return balances[tokenOwner];
    }

    function transfer(address receiver, uint256 numTokens, uint256 timestamp) public returns (bool) {
        require(numTokens <= balances[msg.sender]);
        balances[msg.sender] = balances[msg.sender].sub(numTokens);
        balances[receiver] = balances[receiver].add(numTokens);
        emit Transfer(msg.sender, receiver, numTokens, timestamp);
        return true;
    }

    function approve(address delegate, uint256 numTokens) public returns (bool) {
        allowed[msg.sender][delegate] = numTokens;
        emit Approval(msg.sender, delegate, numTokens);
        return true;
    }

    function allowance(address owner, address delegate) public view returns (uint) {
        return allowed[owner][delegate];
    }

    function transferFrom(address owner, address buyer, uint256 numTokens, uint256 timestamp) public returns (bool) {
        require(numTokens <= balances[owner]);
        require(numTokens <= allowed[owner][msg.sender]);

        balances[owner] = balances[owner].sub(numTokens);
        allowed[owner][msg.sender] = allowed[owner][msg.sender].sub(numTokens);
        balances[buyer] = balances[buyer].add(numTokens);
        emit Transfer(owner, buyer, numTokens, timestamp);
        return true;
    }
}

library SafeMath {
    function sub(uint256 a, uint256 b) internal pure returns (uint256) {
        assert(b <= a);
        return a - b;
    }

    function add(uint256 a, uint256 b) internal pure returns (uint256) {
        uint256 c = a + b;
        assert(c >= a);
        return c;
    }
}