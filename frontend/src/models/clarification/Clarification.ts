import User from '@/models/user/User';
import Question from '../management/Question';

export default class Clarification {
  id: number | null = null;
  content: string = '';
  user!: User;
  question!: Question;
  answered: boolean = false;
  availableByStudent: boolean = false;
  availableByTeacher: boolean = false;

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.user = new User(jsonObj.user);
      this.question = new Question(jsonObj.question);
      this.answered = jsonObj.answered;
      this.availableByStudent = jsonObj.availableByStudent;
      this.availableByTeacher = jsonObj.availableByTeacher;
    }
  }
}
