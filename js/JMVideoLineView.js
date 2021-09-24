import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {
    requireNativeComponent, View, UIManager,
    findNodeHandle,
} from 'react-native';


var RCT_VIDEO_REF = 'JMVideoLineView';

class RNVideoLineView extends Component {
    _onChange(event) {
        alert(event.nativeEvent.progress)//这里获取到传过来的值，就是对应的key

    }

    //然后在RN层写入事件
    _onProgress(event) {
    if (!this.props.onProgress) {
        return;
    }

    this.props.onProgress(event.nativeEvent.progress);
    }

    constructor(props) {
        super(props);
    }


    render() {
        return <JMVideoLineView
            {...this.props}
            onChange={this._onChange.bind(this)}
            onProgress={this._onProgress.bind(this)}
        />;
    };
}

RNVideoLineView.propTypes = {
    onProgress: PropTypes.func,
    ...View.propTypes,

};

var JMVideoLineView = requireNativeComponent('JMVideoLineView', RNVideoLineView,{nativeOnly:{onChange:true}});

module.exports = RNVideoLineView;