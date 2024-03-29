/*
 * Copyright 2013 Websquared, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.fastcatsearch.ir.query;

import java.io.IOException;

import org.fastcatsearch.ir.common.IRException;
import org.fastcatsearch.ir.search.SearchIndexesReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Clause {
	private static Logger logger = LoggerFactory.getLogger(Clause.class);

	public static enum Operator {
		OR, AND, NOT
	};

	private Object operand1;
	private Object operand2;
	private Operator operator;

	public String toString() {

		String str = "";
		if (operand1 != null) {
			str += "{" + operand1.toString();
		} else {
			str += "{";
		}

		if (operator == null)
			return str + "}";

		if (operator == Operator.OR) {
			str += "OR";
		} else if (operator == Operator.AND) {
			str += "AND";
		} else if (operator == Operator.NOT) {
			str += "NOT";
		}

		str += operand2.toString() + "}";

		return str;
	}

	public Clause(Object operand1) {
		this.operand1 = operand1;
	}

	public Clause(Object operand1, Operator operator, Object operand2) {
		this.operand1 = operand1;
		this.operand2 = operand2;
		this.operator = operator;
	}

	public Object operand1() {
		return operand1;
	}

	public Object operand2() {
		return operand2;
	}

	public Operator operator() {
		return operator;
	}

	public OperatedClause getOperatedClause(SearchIndexesReader reader) throws ClauseException, IOException, IRException {
		return getOperatedClause(-1, reader);

	}

	public OperatedClause getOperatedClause(int docCount, SearchIndexesReader reader) throws ClauseException, IOException,
			IRException {
		OperatedClause clause1 = null;
		OperatedClause clause2 = null;

		if (operand1 != null) {
			if (operand1 instanceof Term) {
				Term term = (Term) operand1;
				clause1 = reader.getOperatedClause(term);
				// if(summary != null && (term.option().useSummary() || term.option().useHighlight())){
				// logger.debug("(term.option() = {}", term.option());
				// logger.debug("(term.option() & Term.SUMMARY) = {}", term.option().useSummary());
				// logger.debug("(term.option() & Term.HIGHLIGHT) = {}", term.option().useHighlight());
				// clause1 = reader.getOperatedClause(term);
				// logger.debug("clause1 = {}", clause1);
				// }else{
				// clause1 = reader.getOperatedClause(term);
				// }
			} else {
				clause1 = ((Clause) operand1).getOperatedClause(docCount, reader);
			}
		}

		// if operator is not exist, use only operand1
		if (operator == null)
			return clause1;

		/*
		 * NOT 혼자 쓰이는 연산지원! operand1 이 null일 경우는 Not 연산만 들어올 경우이므로, 전체 문서에서 빼주는 방법으로 연산을 변경한다.
		 */
		if (operand1 == null && operator == Operator.NOT) {
			clause1 = new AllDocumentOperatedClause(docCount);
		}

		if (operand2 instanceof Term) {
			Term term = (Term) operand2;
			clause2 = reader.getOperatedClause(term);
			// if(summary != null && ((term.option() & Term.SUMMARY) > 0 || (term.option() & Term.HIGHLIGHT) > 0)){
			// clause2 = reader.getOperatedClause(term, summary);
			// }else{
			// clause2 = reader.getOperatedClause(term);
			// }
		} else {
			// unary NOT 필드는 두번째 항에 들어올수도 있으므로 docCount를 넣어주도록 한다.
			clause2 = ((Clause) operand2).getOperatedClause(docCount, reader);
		}

		if (operator == Operator.AND)
			return new AndOperatedClause(clause1, clause2);
		else if (operator == Operator.OR)
			return new OrOperatedClause(clause1, clause2);
		else if (operator == Operator.NOT)
			return new NotOperatedClause(clause1, clause2);

		throw new ClauseException("Unknown operator =" + operator);
	}

	public void forceHighlight() {
		if (operand1 != null) {
			if (operand1 instanceof Term) {
				((Term) operand1).addOption(Term.HIGHLIGHT);
			} else {
				((Clause) operand1).forceHighlight();
			}
		}

		if (operand2 != null) {
			if (operand2 instanceof Term) {
				((Term) operand2).addOption(Term.HIGHLIGHT);
			} else {
				((Clause) operand2).forceHighlight();
			}
		}

	}

}
