import * as MutationTypes from './mutation_types.js';


export default {


    [MutationTypes.BIND_WINDOW_WIDTH](state,value){
        state.WINDOW.width=value;
    },

    [MutationTypes.BIND_WINDOW_HEIGHT](state,value){
        state.WIDDOW.height=value;
    },

    [MutationTypes.BIND_WINDOW](state,value){
        state.WINDOW.height=value.height;
        state.WINDOW.width=value.width;
    },

};
