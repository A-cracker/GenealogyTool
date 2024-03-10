import Utils from "./utils";
import {getBandObjId} from "./env";

const EDITING_MEMBERS_KEY = 'editing_members_'+ getBandObjId()
const EDITING_MEMBER_PREFIX = 'editing_member_'

const timeoutMap = new Map()

export default class MemberLocalStorage{
    localStorageAvailable() {
        return Utils.storageAvailable('localStorage');
    }

    getEditingMembers(){
        if(!this.localStorageAvailable()) return;

        const members = localStorage.getItem(EDITING_MEMBERS_KEY)

        return members ? JSON.parse(members) : [];
    }

    getEditingMember(id){
        if(!this.localStorageAvailable()) return;

        return JSON.parse(localStorage.getItem(EDITING_MEMBER_PREFIX + id))
    }

    saveEditingMember(id, member){
        if(!this.localStorageAvailable()) return;

        const that = this

        clearTimeout(timeoutMap.get(id))

        const timeout = setTimeout(function () {
            localStorage.setItem(EDITING_MEMBER_PREFIX + id, JSON.stringify(member))

            let members = that.getEditingMembers()

            const index = members.indexOf(id)

            if (index > -1) return;

            members.push(id)

            localStorage.setItem(EDITING_MEMBERS_KEY, JSON.stringify(members))
        }, 1000)

        timeoutMap.set(id, timeout)
    }

    removeEditingMember(id){
        if(!this.localStorageAvailable()) return;

        localStorage.removeItem(EDITING_MEMBER_PREFIX + id)

        let members = this.getEditingMembers()

        const index = members.indexOf(id)

        if (index === -1) return;

        members.splice(index, 1)

        localStorage.setItem(EDITING_MEMBERS_KEY, JSON.stringify(members))
    }

}