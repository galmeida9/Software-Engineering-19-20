import User from '@/models/user/User';
import Topic from '@/models/management/Topic';
import { ISOtoString } from '@/services/ConvertDateService';

export enum TournamentStatus {
  Open = 'Open',
  Running = 'Running',
  Finished = 'Finished',
  Canceled = 'Canceled'
}
export class Tournament {
  id!: number;
  title!: string;
  numberOfQuestions!: number;
  startingDate!: string | undefined;
  conclusionDate!: string | undefined;
  creator!: User;
  status!: TournamentStatus;
  topics: Topic[] = [];
  signedUpUsers: User[] = [];

  constructor(jsonObj?: Tournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.numberOfQuestions = jsonObj.numberOfQuestions;

      if (jsonObj.startingDate)
        this.startingDate = ISOtoString(jsonObj.startingDate);
      if (jsonObj.conclusionDate)
        this.conclusionDate = ISOtoString(jsonObj.conclusionDate);

      if (jsonObj.status.toString() == 'OPEN')
        this.status = TournamentStatus.Open;
      else if (jsonObj.status.toString() == 'RUNNING')
        this.status = TournamentStatus.Running;
      else if (jsonObj.status.toString() == 'FINISHED')
        this.status = TournamentStatus.Finished;
      else if (jsonObj.status.toString() == 'CANCELED')
        this.status = TournamentStatus.Canceled;
      else this.status = jsonObj.status;
      if (jsonObj.creator) {
        this.creator = new User(jsonObj.creator);
      }
      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
        this.topics = this.topics.sort((topic1, topic2) =>
          topic1.name.localeCompare(topic2.name, 'en', { sensitivity: 'base' })
        );
      }
      if (jsonObj.signedUpUsers) {
        this.signedUpUsers = jsonObj.signedUpUsers.map(
          (user: User) => new User(user)
        );
      }
    }
  }
}
