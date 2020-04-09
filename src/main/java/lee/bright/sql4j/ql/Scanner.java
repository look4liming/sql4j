package lee.bright.sql4j.ql;

import java.util.ArrayList;
import java.util.List;

import lee.bright.sql4j.Sql4jException;

/**
 * @author Bright Lee
 */
public final class Scanner {
	
	private SourceCode sourceCode;
	private String sql;
	private int index;
	
	private TokenType tokenType;
	private int beginIndex;
	private int endIndex;
	
	private StringBuilder buf = new StringBuilder(100);
	
	public Scanner(SourceCode sourceCode) {
		this.sourceCode = sourceCode;
		this.sql = sourceCode.toString();
	}
	
	public void scan() {
		do {
			_scan();
			//System.out.println(getTokenType() + " : " + getContent() + " : " + getBeginIndex());
		} while (tokenType == TokenType._COMMENT_);
	}
	
	private void _scan() {
		skipWhiteCh();
		int ch = readCh();
		if (ch == -1) {
			tokenType = null;
			beginIndex = index - 1;
			return;
		}
		if (ch >= 'A' && ch <= 'Z' || 
			ch >= 'a' && ch <= 'z') {
			beginIndex = index - 1;
			while (true) {
				ch = readCh();
				if (ch == -1) {
					tokenType = TokenType._ID_;
					endIndex = index;
					break;
				}
				if (ch >= 'A' && ch <= 'Z' || 
					ch >= 'a' && ch <= 'z' || 
					ch >= '0' && ch <= '9' || 
					ch == '_') {
					continue;
				}
				if (ch == -1) {
					endIndex = index;
					tokenType = TokenType._ID_;
					String content = getContent();
					TokenType tt = TokenType.getTokenType(content);
					if (tt != null) {
						tokenType = tt;
					}
					return;
				}
				if (isSeparatorCh(ch)) {
					endIndex = index - 1;
					tokenType = TokenType._ID_;
					String content = getContent();
					TokenType tt = TokenType.getTokenType(content);
					if (tt != null) {
						tokenType = tt;
					}
					backCh();
					return;
				}
				IndexableMessage msg = new IndexableMessage(
						index - 1, 
						"Invalid character.");
				List<IndexableMessage> list = 
						new ArrayList<IndexableMessage>(1);
				list.add(msg);
				throw new Sql4jException(sourceCode, list);
			}
			return;
		}
		if (ch == '?') {
			beginIndex = index - 1;
			ch = readCh();
			if (ch == -1 || isSeparatorCh(ch)) {
				IndexableMessage msg = new IndexableMessage(
						index, 
						"Missing parameter name.");
				List<IndexableMessage> list = 
						new ArrayList<IndexableMessage>(1);
				list.add(msg);
				throw new Sql4jException(sourceCode, list);
			}
			if (!(ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z')) {
				IndexableMessage msg = new IndexableMessage(
						index - 1, 
						"Invalid character.");
				List<IndexableMessage> list = 
						new ArrayList<IndexableMessage>(1);
				list.add(msg);
				throw new Sql4jException(sourceCode, list);
			}
			while (true) {
				ch = readCh();
				if (ch == -1) {
					endIndex = index;
					tokenType = TokenType._PARAM_;
					break;
				}
				if (ch >= 'A' && ch <= 'z' || 
					ch >= 'a' && ch <= 'z' || 
					ch >= '0' && ch <= '9' || 
					ch == '_') {
					continue;
				}
				if (ch == -1) {
					endIndex = index;
					tokenType = TokenType._PARAM_;
					return;
				}
				if (isSeparatorCh(ch)) {
					endIndex = index - 1;
					tokenType = TokenType._PARAM_;
					backCh();
					return;
				}
				IndexableMessage msg = new IndexableMessage(
						index - 1, 
						"Invalid character.");
				List<IndexableMessage> list = 
						new ArrayList<IndexableMessage>(1);
				list.add(msg);
				throw new Sql4jException(sourceCode, list);
			}
			return;
		}
		if (ch == '0') {
			beginIndex = index - 1;
			ch = readCh();
			if (ch == -1) {
				endIndex = index;
				tokenType = TokenType._NUM_;
				return;
			}
			if (ch == '.') {
				ch = readCh();
				if (!(ch >= '0' && ch <= '9')) {
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"Digit expected here.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
				while (true) {
					ch = readCh();
					if (ch >= '0' && ch <= '9') {
						continue;
					}
					if (ch == -1) {
						endIndex = index;
						tokenType = TokenType._NUM_;
						return;
					}
					if (isSeparatorCh(ch)) {
						endIndex = index - 1;
						tokenType = TokenType._NUM_;
						backCh();
						return;
					}
					if (ch == 'E' || ch == 'e') {
						break;
					}
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"Invalid character.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
			}
			if (ch == -1) {
				endIndex = index;
				tokenType = TokenType._NUM_;
				return;
			}
			if (isSeparatorCh(ch)) {
				endIndex = index - 1;
				tokenType = TokenType._NUM_;
				backCh();
				return;
			}
			if (ch == 'E' || ch == 'e') {
				ch = readCh();
				if (ch == '+' || ch == '-') {
					ch = readCh();
				}
				if (ch == '0') {
					ch = readCh();
					if (ch != -1 && !isSeparatorCh(ch)) {
						IndexableMessage msg = new IndexableMessage(
								index - 1, 
								"Invalid character.");
						List<IndexableMessage> list = 
								new ArrayList<IndexableMessage>(1);
						list.add(msg);
						throw new Sql4jException(sourceCode, list);
					}
					backCh();
					endIndex = index;
					tokenType = TokenType._NUM_;
					return;
				}
				if (!(ch >= '1' && ch <= '9')) {
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"Digit expected here.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
				while (true) {
					ch = readCh();
					if (ch >= '0' && ch <= '9') {
						continue;
					}
					if (ch == -1) {
						endIndex = index;
						tokenType = TokenType._NUM_;
						return;
					}
					if (isSeparatorCh(ch)) {
						endIndex = index - 1;
						tokenType = TokenType._NUM_;
						backCh();
						return;
					}
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"Invalid character.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
			}
			if (ch == -1) {
				endIndex = index;
				tokenType = TokenType._NUM_;
				return;
			}
			if (isSeparatorCh(ch)) {
				endIndex = index - 1;
				tokenType = TokenType._NUM_;
				backCh();
				return;
			}
			IndexableMessage msg = new IndexableMessage(
					index - 1, 
					"Invalid character.");
			List<IndexableMessage> list = 
					new ArrayList<IndexableMessage>(1);
			list.add(msg);
			throw new Sql4jException(sourceCode, list);
		}
		if (ch >= '1' && ch <= '9') {
			beginIndex = index - 1;
			while (true) {
				ch = readCh();
				if (ch >= '0' && ch <= '9') {
					continue;
				}
				if (ch == -1) {
					endIndex = index;
					tokenType = TokenType._NUM_;
					return;
				}
				if (ch == '.' || ch == 'E' || ch == 'e') {
					break;
				}
				if (isSeparatorCh(ch)) {
					endIndex = index - 1;
					tokenType = TokenType._NUM_;
					backCh();
					return;
				}
			}
			if (ch == -1) {
				endIndex = index;
				tokenType = TokenType._NUM_;
				return;
			}
			if (ch == '.') {
				ch = readCh();
				if (!(ch >= '0' && ch <= '9')) {
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"Digit expected here.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
				while (true) {
					ch = readCh();
					if (ch >= '0' && ch <= '9') {
						continue;
					}
					if (ch == -1) {
						endIndex = index;
						tokenType = TokenType._NUM_;
						return;
					}
					if (isSeparatorCh(ch)) {
						endIndex = index - 1;
						tokenType = TokenType._NUM_;
						backCh();
						return;
					}
					if (ch == 'E' || ch == 'e') {
						break;
					}
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"Invalid character.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
			}
			if (ch == -1) {
				endIndex = index;
				tokenType = TokenType._NUM_;
				return;
			}
			if (isSeparatorCh(ch)) {
				endIndex = index - 1;
				tokenType = TokenType._NUM_;
				backCh();
				return;
			}
			if (ch == 'E' || ch == 'e') {
				ch = readCh();
				if (ch == '+' || ch == '-') {
					ch = readCh();
				}
				if (ch == '0') {
					ch = readCh();
					if (ch != -1 && !isSeparatorCh(ch)) {
						IndexableMessage msg = new IndexableMessage(
								index - 1, 
								"Invalid character.");
						List<IndexableMessage> list = 
								new ArrayList<IndexableMessage>(1);
						list.add(msg);
						throw new Sql4jException(sourceCode, list);
					}
					backCh();
					endIndex = index;
					tokenType = TokenType._NUM_;
					return;
				}
				if (!(ch >= '1' && ch <= '9')) {
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"Digit expected here.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
				while (true) {
					ch = readCh();
					if (ch >= '0' && ch <= '9') {
						continue;
					}
					if (ch == -1) {
						endIndex = index;
						tokenType = TokenType._NUM_;
						return;
					}
					if (isSeparatorCh(ch)) {
						endIndex = index - 1;
						tokenType = TokenType._NUM_;
						backCh();
						return;
					}
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"Invalid character.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
			}
			if (ch == -1) {
				endIndex = index;
				tokenType = TokenType._NUM_;
				return;
			}
			if (isSeparatorCh(ch)) {
				endIndex = index - 1;
				tokenType = TokenType._NUM_;
				backCh();
				return;
			}
			IndexableMessage msg = new IndexableMessage(
					index - 1, 
					"Invalid character.");
			List<IndexableMessage> list = 
					new ArrayList<IndexableMessage>(1);
			list.add(msg);
			throw new Sql4jException(sourceCode, list);
		}
		if (ch == '\'') {
			buf.setLength(0);
			beginIndex = index - 1;
			while (true) {
				ch = readCh();
				if (ch == '\n' || ch == -1) {
					IndexableMessage msg = new IndexableMessage(
							index - 1, 
							"' expected here.");
					List<IndexableMessage> list = 
							new ArrayList<IndexableMessage>(1);
					list.add(msg);
					throw new Sql4jException(sourceCode, list);
				}
				if (ch == '\'') {
					ch = readCh();
					if (ch == '\'') {
						buf.append('\'');
						continue;
					}
					if (ch != -1) {
						backCh();
					}
					endIndex = index;
					tokenType = TokenType._STR_;
					return;
				}
				if (ch == '\\') {
					ch = readCh();
					if (ch == 'b') {
						buf.append('\b');
					} else if (ch == 't') {
						buf.append('\t');
					} else if (ch == 'n') {
						buf.append('\n');
					} else if (ch == 'f') {
						buf.append('\f');
					} else if (ch == 'r') {
						buf.append('\r');
					} else if (ch == '"') {
						buf.append('\"');
					} else if (ch == '\'') {
						buf.append('\'');
					} else if (ch == '\\') {
						buf.append('\\');
					} else {
						IndexableMessage msg = new IndexableMessage(
								index - 2, 
								"\\b, \\t, \\n, \\f, \\r, \\\", \\', \\\\ expected here.");
						List<IndexableMessage> list = 
								new ArrayList<IndexableMessage>(1);
						list.add(msg);
						throw new Sql4jException(sourceCode, list);
					}
				} else {
					buf.append((char) ch);
				}
			}
		}
		if (ch == '&') {
			beginIndex = index - 1;
			tokenType = TokenType._AMPERSAND_;
			endIndex = index;
			return;
		}
		if (ch == '*') {
			beginIndex = index - 1;
			tokenType = TokenType._ASTERISK_;
			endIndex = index;
			return;
		}
		if (ch == '^') {
			beginIndex = index - 1;
			tokenType = TokenType._CIRCUMFLEX_;
			endIndex = index;
			return;
		}
		if (ch == ':') {
			beginIndex = index - 1;
			ch = readCh();
			if (ch == ':') {
				tokenType = TokenType._DOUBLE_COLON_;
				endIndex = index;
				return;
			}
			tokenType = TokenType._COLON_;
			if (ch != -1) {
				endIndex = index - 1;
				backCh();
			} else {
				endIndex = index;
			}
			return;
		}
		if (ch == ',') {
			beginIndex = index - 1;
			tokenType = TokenType._COMMA_;
			endIndex = index;
			return;
		}
		if (ch == '|') {
			beginIndex = index - 1;
			ch = readCh();
			if (ch == '|') {
				tokenType = TokenType._CONCATENATION_OPERATOR_;
				endIndex = index;
				return;
			}
			tokenType = TokenType._VERTICAL_BAR_;
			if (ch != -1) {
				endIndex = index - 1;
				backCh();
			} else {
				endIndex = index;
			}
			return;
		}
		if (ch == '.') {
			beginIndex = index - 1;
			ch = readCh();
			if (ch == '.') {
				tokenType = TokenType._DOUBLE_PERIOD_;
				endIndex = index;
				return;
			}
			tokenType = TokenType._PERIOD_;
			if (ch != -1) {
				endIndex = index - 1;
				backCh();
			} else {
				endIndex = index;
			}
			return;
		}
		if (ch == '"') {
			beginIndex = index - 1;
			tokenType = TokenType._DOUBLE_QUOTE_;
			endIndex = index;
			return;
		}
		if (ch == '=') {
			beginIndex = index - 1;
			tokenType = TokenType._EQUALS_OPERATOR_;
			endIndex = index;
			return;
		}
		if (ch == '=') {
			beginIndex = index - 1;
			tokenType = TokenType._EQUALS_OPERATOR_;
			endIndex = index;
			return;
		}
		if (ch == '>') {
			beginIndex = index - 1;
			ch = readCh();
			if (ch == '=') {
				tokenType = TokenType._GREATER_THAN_OR_EQUALS_OPERATOR_;
				endIndex = index;
				return;
			}
			tokenType = TokenType._GREATER_THAN_OPERATOR_;
			if (ch != -1) {
				endIndex = index - 1;
				backCh();
			} else {
				endIndex = index;
			}
			return;
		}
		if (ch == '{') {
			beginIndex = index - 1;
			tokenType = TokenType._LEFT_BRACE_;
			endIndex = index;
			return;
		}
		if (ch == '[') {
			beginIndex = index - 1;
			tokenType = TokenType._LEFT_BRACKET_;
			endIndex = index;
			return;
		}
		if (ch == '(') {
			beginIndex = index - 1;
			tokenType = TokenType._LEFT_PAREN_;
			endIndex = index;
			return;
		}
		if (ch == '<') {
			beginIndex = index - 1;
			ch = readCh();
			if (ch == '=') {
				tokenType = TokenType._LESS_THAN_OR_EQUALS_OPERATOR_;
				endIndex = index;
				return;
			}
			if (ch == '>') {
				tokenType = TokenType._NOT_EQUALS_OPERATOR_;
				endIndex = index;
				return;
			}
			tokenType = TokenType._LESS_THAN_OPERATOR_;
			if (ch != -1) {
				endIndex = index - 1;
				backCh();
			} else {
				endIndex = index;
			}
			return;
		}
		if (ch == '-') {
			beginIndex = index - 1;
			ch = readCh();
			if (ch == '>') {
				tokenType = TokenType._RIGHT_ARROW_;
				endIndex = index;
				return;
			}
			if (ch == '-') {
				while (true) {
					ch = readCh();
					if (ch == '\n' || ch == -1) {
						break;
					}
				}
				tokenType = TokenType._COMMENT_;
				if (ch == -1) {
					endIndex = index;
				} else {
					endIndex = index - 1;
				}
				return;
			}
			tokenType = TokenType._MINUS_SIGN_;
			if (ch != -1) {
				endIndex = index - 1;
				backCh();
			} else {
				endIndex = index;
			}
			return;
		}
		if (ch == '%') {
			beginIndex = index - 1;
			tokenType = TokenType._PERCENT_;
			endIndex = index;
			return;
		}
		if (ch == '+') {
			beginIndex = index - 1;
			tokenType = TokenType._PLUS_SIGN_;
			endIndex = index;
			return;
		}
		if (ch == '}') {
			beginIndex = index - 1;
			tokenType = TokenType._RIGHT_BRACE_;
			endIndex = index;
			return;
		}
		if (ch == ']') {
			beginIndex = index - 1;
			tokenType = TokenType._RIGHT_BRACKET_;
			endIndex = index;
			return;
		}
		if (ch == ')') {
			beginIndex = index - 1;
			tokenType = TokenType._RIGHT_PAREN_;
			endIndex = index;
			return;
		}
		if (ch == ';') {
			beginIndex = index - 1;
			tokenType = TokenType._SEMICOLON_;
			endIndex = index;
			return;
		}
		if (ch == '/') {
			ch = readCh();
			if (ch == '*') {
				while (true) {
					ch = readCh();
					if (ch == '*') {
						ch = readCh();
						if (ch == '/') {
							break;
						}
						if (ch == -1) {
							IndexableMessage msg = new IndexableMessage(
									index - 1, 
									"missing comment end flag.");
							List<IndexableMessage> list = 
									new ArrayList<IndexableMessage>(1);
							list.add(msg);
							throw new Sql4jException(sourceCode, list);
						}
					}
				}
				tokenType = TokenType._COMMENT_;
				endIndex = index;
				return;
			}
			backCh();
			beginIndex = index - 1;
			tokenType = TokenType._SOLIDUS_;
			endIndex = index;
			return;
		}
		if (ch == '_') {
			beginIndex = index - 1;
			tokenType = TokenType._UNDERSCORE_;
			endIndex = index;
			return;
		}
		IndexableMessage msg = new IndexableMessage(
				index - 1, 
				"Invalid character.");
		List<IndexableMessage> list = 
				new ArrayList<IndexableMessage>(1);
		list.add(msg);
		throw new Sql4jException(sourceCode, list);
	}
	
	public SourceCode getSourceCode() {
		return sourceCode;
	}
	
	public TokenType getTokenType() {
		return tokenType;
	}
	
	public int getBeginIndex() {
		return beginIndex;
	}
	
	public int getEndIndex() {
		return endIndex;
	}
	
	public String getContent() {
		String content;
		if (tokenType == TokenType._STR_) {
			content = buf.toString();
		} else if (tokenType == TokenType._PARAM_) {
			content = sql.substring(
					beginIndex + 1, endIndex);
		} else if (tokenType == TokenType._COMMENT_) {
			String txt = sql.substring(
					beginIndex, endIndex);
			if (txt.startsWith("--")) {
				content = sql.substring(
						beginIndex + 2, endIndex);
			} else {
				content = sql.substring(
						beginIndex + 2, endIndex - 2);
			}
		} else {
			content = sql.substring(beginIndex, endIndex);
		}
		return content;
	}
	
	private void skipWhiteCh() {
		while (true) {
			int ch = readCh();
			if (ch == -1) {
				break;
			}
			if (ch != ' ' && ch != '\t' && 
				ch != '\n' && ch != '\r') {
				backCh();
				break;
			}
		}
	}
	
	private int readCh() {
		if (index >= sql.length()) {
			return -1;
		}
		char ch = sql.charAt(index);
		index++;
		return ch;
	}
	
	private void backCh() {
		if (index <= 0) {
			return;
		}
		index--;
	}
	
	private boolean isSeparatorCh(int ch) {
		if (ch == '&' || ch == '*' || ch == '^' ||
			ch == ':' || ch == ',' || ch == '|' ||
			ch == '.' || ch == '"' || ch == '=' ||
			ch == '>' || ch == '{' || ch == '[' ||
			ch == '(' || ch == '<' || ch == '-' ||
			ch == '%' || ch == '+' || ch == '?' ||
			ch == '}' || ch == ']' || ch == ')' ||
			ch == ';' || ch == '/' || ch == '_' ||
			ch == ' ' || ch == '\t' || ch == '\n' ||
			ch == '\'') {
			return true;
		}
		return false;
	}

}
